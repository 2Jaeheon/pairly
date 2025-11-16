package pairly.client.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import pairly.client.ClientState;
import pairly.client.parser.CommandParser;
import pairly.client.ui.ConsoleView;
import pairly.common.exception.InvalidCommandException;
import pairly.common.message.ClientMessage;
import pairly.common.message.MessageType;
import pairly.common.util.JsonConverter;
import pairly.domain.EditorState;
import pairly.domain.command.Command;
import pairly.domain.command.MarkCommand;
import pairly.domain.command.QuitCommand;
import pairly.domain.command.SyncCommand;

public class InputHandler implements Runnable {

    private final PrintWriter out;
    private final BufferedReader consoleReader;
    private final CommandParser parser;
    private final JsonConverter jsonConverter;
    private final ConsoleView consoleView;
    private final AtomicReference<ClientState> clientStateHolder;

    private static final String MARK_DELIMITER = "|";

    public InputHandler(Socket socket,
                        JsonConverter jsonConverter,
                        ConsoleView consoleView,
                        AtomicReference<ClientState> clientStateHolder,
                        AtomicReference<EditorState> editorStateHolder,
                        CommandParser commandParser) throws IOException {
        this.out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
        this.consoleReader = new BufferedReader(new InputStreamReader(System.in));
        this.jsonConverter = jsonConverter;
        this.consoleView = consoleView;
        this.clientStateHolder = clientStateHolder;
        this.parser = commandParser;
    }

    @Override
    public void run() {
        consoleView.redraw();

        try {
            String line;
            while ((line = consoleReader.readLine()) != null) {
                try {
                    // input -> Command
                    Command command = parser.parse(line);
                    // Command -> DTO
                    ClientMessage message = convertToDto(command);
                    // DTO -> JSON
                    String jsonMessage = jsonConverter.toJson(message);
                    // JSON -> 서버 전송
                    out.println(jsonMessage);
                    // :quit 명령어면 루프 종료
                    if (command instanceof QuitCommand) {
                        break;
                    }

                    clientStateHolder.updateAndGet(old ->
                            new ClientState(old.role(), old.leftTime(), "명령 전송 완료.")
                    );

                } catch (InvalidCommandException e) {
                    System.err.println("입력 오류: " + e.getMessage());
                    clientStateHolder.updateAndGet(oldState ->
                            new ClientState(oldState.role(), oldState.leftTime(), "[에러] " + e.getMessage())
                    );
                }

                consoleView.redraw();
            }
        } catch (IOException e) {
            System.err.println("콘솔 입력 또는 서버 전송 오류: " + e.getMessage());
        }
    }

    private ClientMessage convertToDto(Command command) {
        if (command instanceof SyncCommand syncCommand) {
            String payload = String.join("\n", syncCommand.getCodeLines());
            return new ClientMessage(MessageType.SYNC, payload);
        }

        if (command instanceof MarkCommand markCommand) {
            String payload = markCommand.getLineNumber() + MARK_DELIMITER + markCommand.getComment();
            return new ClientMessage(MessageType.MARK, payload);
        }

        if (command instanceof QuitCommand) {
            return new ClientMessage(MessageType.QUIT, "");
        }

        throw new IllegalArgumentException("알 수 없는 명령어 타입입니다.");
    }
}

