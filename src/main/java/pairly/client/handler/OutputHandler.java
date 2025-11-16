package pairly.client.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import pairly.client.ClientState;
import pairly.client.ui.ConsoleView;
import pairly.common.message.MarkerUpdateDto;
import pairly.common.message.ServerMessage;
import pairly.common.util.JsonConverter;
import pairly.domain.EditorState;
import pairly.domain.Marker;
import pairly.domain.Role;

public class OutputHandler implements Runnable {
    private final BufferedReader in;
    private final JsonConverter jsonConverter;
    private final ConsoleView consoleView;
    private final AtomicReference<ClientState> clientStateHolder;
    private final AtomicReference<EditorState> editorStateHolder;

    public OutputHandler(Socket socket,
                         JsonConverter jsonConverter,
                         ConsoleView consoleView,
                         AtomicReference<ClientState> clientStateHolder,
                         AtomicReference<EditorState> editorStateHolder) throws IOException {

        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.jsonConverter = jsonConverter;
        this.consoleView = consoleView;
        this.clientStateHolder = clientStateHolder;
        this.editorStateHolder = editorStateHolder;
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                try {
                    // JSON -> DTO
                    ServerMessage msg = jsonConverter.fromJson(line, ServerMessage.class);
                    processServerMessage(msg);

                } catch (Exception e) {
                    System.err.println("서버 메시지 처리 오류: " + e.getMessage());
                    clientStateHolder.updateAndGet(old ->
                            old.withStatusMessage("[파싱 오류] " + e.getMessage())
                    );
                }

                consoleView.redraw();
            }
        } catch (IOException e) {
            System.err.println("서버 연결이 끊겼습니다: " + e.getMessage());
            clientStateHolder.set(ClientState.initialState().withStatusMessage("서버 연결이 끊겼습니다."));
            consoleView.redraw();
        }
    }

    private void processServerMessage(ServerMessage msg) {
        String payload = msg.getMessage();
        EditorState currentEditor = editorStateHolder.get();

        switch (msg.getType()) {
            case ROLE_SWAP:
                Role newRole = Role.valueOf(payload);
                clientStateHolder.updateAndGet(old ->
                        old.withRole(newRole).withStatusMessage("역할이 교대되었습니다: " + newRole.name())
                );
                break;

            case TIMER_TICK:
                clientStateHolder.updateAndGet(old -> old.withTime(payload));
                break;

            case SYNC_UPDATE:
                List<String> lines = Arrays.asList(payload.split("\n"));
                currentEditor.syncCode(lines);
                break;

            case MARK_UPDATE:
                MarkerUpdateDto[] updates = jsonConverter.fromJson(payload, MarkerUpdateDto[].class);

                currentEditor.clearMarkers();
                for (MarkerUpdateDto dto : updates) {
                    currentEditor.addMarker(
                            dto.getLineNumber(),
                            new Marker(dto.getComment())
                    );
                }
                break;

            case ERROR:
                clientStateHolder.updateAndGet(old -> old.withStatusMessage("[서버 에러] " + payload));
                break;

            default:
                System.out.println("알 수 없는 서버 메시지 수신: " + msg.getType());
        }
    }
}
