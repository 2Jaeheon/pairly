package pairly.client.ui;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import pairly.client.ClientState;
import pairly.common.util.AnsiConsole;
import pairly.domain.EditorState;
import pairly.domain.Marker;
import pairly.domain.Role;

public class ConsoleView {

    private static final int CODE_REVIEW_HEIGHT = 20;
    private static final String SEPARATOR = "=".repeat(70);

    // 최산 상태를 보관하는 홀더
    private final AtomicReference<ClientState> clientStateHolder;
    private final AtomicReference<EditorState> editorStateHolder;

    public ConsoleView(AtomicReference<ClientState> clientStateHolder,
                       AtomicReference<EditorState> editorStateHolder) {
        this.clientStateHolder = clientStateHolder;
        this.editorStateHolder = editorStateHolder;
    }

    public void redraw() {
        // 홀더에서 상태를 가지고 옴
        final ClientState currentState = clientStateHolder.get();
        final EditorState currentEditorState = editorStateHolder.get();

        AnsiConsole.clearScreen();

        // 헤더 그리기
        drawHeader(currentState);

        // 코드 뷰어 그리기
        drawCodeViewer(currentEditorState);

        // 상태 및 입력 프롬프트 그리기
        drawFooter(currentState);
    }

    public void redrawHeaderOnly(ClientState state) {
        AnsiConsole.printAndFlush(AnsiConsole.SAVE_CURSOR);

        AnsiConsole.printAndFlush(AnsiConsole.moveTo(1, 1));

        String headerLine = AnsiConsole.colorYellow(
                "[ ROLE: " + state.getRoleDisplayName() + " | TIME LEFT: " + state.leftTime() + " ]");
        AnsiConsole.printAndFlush(headerLine + AnsiConsole.CLEAR_LINE);

        AnsiConsole.printAndFlush(AnsiConsole.moveTo(2, 1));
        AnsiConsole.printAndFlush(SEPARATOR + AnsiConsole.CLEAR_LINE);

        AnsiConsole.printAndFlush(AnsiConsole.RESTORE_CURSOR);
    }

    private void drawHeader(ClientState state) {
        String roleName = state.getRoleDisplayName();
        String time = state.leftTime();

        System.out.println(AnsiConsole.colorYellow("[ ROLE: " + roleName + " | TIME LEFT: " + time + " ]"));
        System.out.println(SEPARATOR);
    }

    private void drawCodeViewer(EditorState state) {
        List<String> codeLines = state.getCodeLines();
        Map<Integer, Marker> marks = state.getMarks();

        for (int i = 0; i < CODE_REVIEW_HEIGHT; i++) {
            int lineNumber = i + 1;
            String line = "~";
            if (i < codeLines.size()) {
                line = codeLines.get(i);
            }

            Marker marker = marks.get(lineNumber);
            String formattedMarker = formatMarker(marker);

            System.out.printf("%3d: %-60s %s%n", lineNumber, line, formattedMarker);
        }
        System.out.println(SEPARATOR);
    }

    private void drawFooter(ClientState state) {
        System.out.println("[STATUS] " + state.statusMessage());

        System.out.print(AnsiConsole.colorGreen("> "));
    }

    // 어떻게 보일지는 consoleView가 결정함
    private String formatMarker(Marker marker) {
        if (marker == null) {
            return "";
        }

        return AnsiConsole.colorCyan("\"" + marker.getComment() + "\"");
    }
}
