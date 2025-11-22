package pairly.server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pairly.common.exception.InvalidCommandException;
import pairly.common.message.ClientMessage;
import pairly.common.message.MarkerUpdateDto;
import pairly.common.message.MessageType;
import pairly.common.message.ServerMessage;
import pairly.common.util.JsonConverter;
import pairly.domain.EditorState;
import pairly.domain.Role;
import pairly.domain.command.Command;
import pairly.domain.command.CommandFactory;
import pairly.domain.command.MarkCommand;
import pairly.domain.command.QuitCommand;
import pairly.domain.command.SyncCommand;

public class SessionManager {

    private ClientSession clientA = null;
    private ClientSession clientB = null;

    private final JsonConverter jsonConverter;
    private final PomodoroTimer pomodoroTimer;
    private final RoleManager roleManager;
    private final EditorState editorState;
    private final CommandFactory commandFactory;
    private final Map<ClientSession, Role> roles = new HashMap<>();

    public SessionManager(JsonConverter jsonConverter,
                          PomodoroTimer pomodoroTimer,
                          RoleManager roleManager,
                          EditorState editorState,
                          CommandFactory commandFactory) {
        this.jsonConverter = jsonConverter;
        this.pomodoroTimer = pomodoroTimer;
        this.roleManager = roleManager;
        this.editorState = editorState;
        this.commandFactory = commandFactory;
    }

    public synchronized void handleNewConnection(Socket socket) {
        System.out.println("연결 처리 중...");

        ClientSession newSession = new ClientSession(socket, this, jsonConverter);

        if (clientA == null) {
            clientA = newSession;
            new Thread(clientA).start();
            clientA.sendRawMessage("파트너를 기다리는 중입니다...");
            System.out.println("Client A 연결됨. 대기 시작.");

        } else if (clientB == null) {
            clientB = newSession;
            new Thread(clientB).start();
            System.out.println("Client B 연결됨. 세션 시작.");

            this.startSession();
        } else {
            System.out.println("세션이 꽉 차서 새 연결을 거부합니다.");
            newSession.sendRawMessage("세션이 꽉 찼습니다. 나중에 다시 시도해주세요.");
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("거부된 소켓을 닫는 중 오류 발생: " + e.getMessage());
            }
        }
    }

    public synchronized void processMessage(ClientSession sender, ClientMessage msg) {
        try {
            Role senderRole = roles.get(sender);
            if (senderRole == null) {
                throw new InvalidCommandException("아직 역할이 할당되지 않았습니다.");
            }

            Command command = commandFactory.create(msg);
            roleManager.validate(senderRole, command);
            command.execute(this.editorState);

            ServerMessage updateMsg = createUpdateMessage(command);

            if (updateMsg != null) {
                broadcast(updateMsg);
            }
        } catch (InvalidCommandException e) {
            System.err.println("[권한 오류] " + e.getMessage());
            sender.sendMessage(new ServerMessage(MessageType.ERROR, e.getMessage()));
        } catch (Exception e) {
            System.err.println("[서버 오류] " + e.getMessage());
            e.printStackTrace();
            sender.sendMessage(new ServerMessage(MessageType.ERROR, "알 수 없는 서버 오류입니다."));
        }
    }

    private ServerMessage createUpdateMessage(Command command) {
        if (command instanceof SyncCommand) {
            String fullCode = String.join("\n", editorState.getCodeLines());
            return new ServerMessage(MessageType.SYNC_UPDATE, fullCode);
        }

        if (command instanceof MarkCommand) {
            List<MarkerUpdateDto> dtos = editorState.getMarks().entrySet().stream()
                    .map(entry -> new MarkerUpdateDto(
                            entry.getKey(),
                            entry.getValue().getComment()
                    ))
                    .toList();

            String markersJson = jsonConverter.toJson(dtos);
            return new ServerMessage(MessageType.MARK_UPDATE, markersJson);
        }

        if (command instanceof QuitCommand) {
            return null;
        }

        throw new IllegalStateException("알 수 없는 명령어 타입입니다");
    }

    public synchronized void clientDisconnected(ClientSession session) {
        System.out.println("클라이언트 연결 종료 감지");
        boolean sessionWasActive = (clientA != null && clientB != null);
        ClientSession remainingClient = null;

        if (session == clientA) {
            clientA = null;
            remainingClient = clientB;
            System.out.println("Client A 연결 종료됨.");
        } else if (session == clientB) {
            clientB = null;
            remainingClient = clientA;
            System.out.println("Client B 연결 종료됨.");
        } else {
            return;
        }

        pomodoroTimer.stop();
        System.out.println("Pomodoro 타이머 중지됨");

        if (sessionWasActive) {
            remainingClient.sendMessage(new ServerMessage(
                    MessageType.ERROR,
                    "파트너의 연결이 끊겼습니다. 세션을 종료합니다."
            ));

            if (remainingClient == clientB) {
                clientA = clientB;
                clientB = null;
                clientA.sendMessage(new ServerMessage(
                        MessageType.ERROR,
                        "파트너를 기다리는 중입니다..."
                ));
                System.out.println("Client B를 Client A로 이동, 다시 대기 시작.");
            } else {
                clientA.sendMessage(new ServerMessage(
                        MessageType.ERROR,
                        "파트너를 기다리는 중입니다..."
                ));
                System.out.println("Client A가 파트너를 기다립니다.");
            }
        }
    }

    private void startSession() {
        System.out.println("세션 시작됨 (A, B 모두 접속)");

        // 초기 역할 할당
        roles.put(clientA, Role.CODE_WRITER);
        roles.put(clientB, Role.CODE_REVIEWER);

        // Pomodoro 타이머 시작 및 콜백 등록
        pomodoroTimer.start(this::broadcastTime, this::swapRoles);

        // 초기 역할 전송
        clientA.sendMessage(new ServerMessage(MessageType.ROLE_SWAP, Role.CODE_WRITER.name()));
        clientB.sendMessage(new ServerMessage(MessageType.ROLE_SWAP, Role.CODE_REVIEWER.name()));

        // 초기 에디터 상태 전송
        String initialCode = String.join("\n", editorState.getCodeLines());
        broadcast(new ServerMessage(MessageType.SYNC_UPDATE, initialCode));
    }

    private void broadcastTime(String time) {
        ServerMessage timeMsg = new ServerMessage(MessageType.TIMER_TICK, time);
        broadcast(timeMsg);
    }

    private synchronized void swapRoles() {
        if (clientA == null || clientB == null) {
            System.out.println("역할 교대 시도 중단");
            return;
        }

        System.out.println("시간 만료! 역할 교대 실행.");

        Role newRoleA = roleManager.swapRole(roles.get(clientA));
        roles.put(clientA, newRoleA);
        Role newRoleB = roleManager.swapRole(roles.get(clientB));
        roles.put(clientB, newRoleB);

        clientA.sendMessage(new ServerMessage(MessageType.ROLE_SWAP, newRoleA.name()));
        clientB.sendMessage(new ServerMessage(MessageType.ROLE_SWAP, newRoleB.name()));

        editorState.clearMarkers();
        String emptyMarkersJson = jsonConverter.toJson(List.of());
        broadcast(new ServerMessage(MessageType.MARK_UPDATE, emptyMarkersJson));
    }

    private void broadcast(ServerMessage message) {
        if (clientA != null) {
            clientA.sendMessage(message);
        }
        if (clientB != null) {
            clientB.sendMessage(message);
        }
    }
}
