package pairly.server;

import java.io.IOException;
import java.net.Socket;
import pairly.common.message.ClientMessage;
import pairly.common.util.JsonConverter;

public class SessionManager {

    private ClientSession clientA = null;
    private ClientSession clientB = null;

    private final JsonConverter jsonConverter;

    public SessionManager() {
        this.jsonConverter = new JsonConverter();
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
        // 임시: 미구현 상태
        System.out.println("[SessionManager] 메시지 수신" + msg.getType());
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

        if (sessionWasActive) {
            remainingClient.sendRawMessage("파트너의 연결이 끊겼습니다. 세션을 종료합니다.");

            if (remainingClient == clientB) {
                clientA = clientB;
                clientB = null;
                clientA.sendRawMessage("파트너를 기다리는 중입니다...");
                System.out.println("Client B를 Client A로 이동, 다시 대기 시작.");
            } else {
                clientA.sendRawMessage("파트너를 기다리는 중입니다...");
                System.out.println("Client A가 파트너를 기다립니다.");
            }
        }
    }

    private void startSession() {
        System.out.println("세션 시작됨 (A, B 모두 접속)");
    }
}
