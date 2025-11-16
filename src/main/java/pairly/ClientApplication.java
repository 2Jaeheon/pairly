package pairly;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicReference;
import pairly.client.ClientState;
import pairly.client.handler.InputHandler;
import pairly.client.handler.OutputHandler;
import pairly.client.parser.CommandParser;
import pairly.client.ui.ConsoleView;
import pairly.common.util.JsonConverter;
import pairly.domain.EditorState;

public class ClientApplication {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            System.out.println("서버에 연결되었습니다.");

            AtomicReference<ClientState> clientStateHolder =
                    new AtomicReference<>(ClientState.initialState());
            AtomicReference<EditorState> editorStateHolder =
                    new AtomicReference<>(new EditorState());

            JsonConverter jsonConverter = new JsonConverter();
            CommandParser commandParser = new CommandParser();
            ConsoleView consoleView = new ConsoleView(clientStateHolder, editorStateHolder);

            OutputHandler outputHandler = new OutputHandler(
                    socket,
                    jsonConverter,
                    consoleView,
                    clientStateHolder,
                    editorStateHolder
            );
            new Thread(outputHandler).start();

            InputHandler inputHandler = new InputHandler(
                    socket,
                    jsonConverter,
                    consoleView,
                    clientStateHolder,
                    editorStateHolder,
                    commandParser
            );
            inputHandler.run();

        } catch (UnknownHostException e) {
            System.err.println("서버를 찾을 수 없습니다: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O 오류: " + e.getMessage());
        } finally {
            System.out.println("연결을 종료합니다.");
        }
    }
}
