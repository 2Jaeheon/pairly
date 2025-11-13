package pairly.client.ui;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pairly.client.ClientState;
import pairly.domain.EditorState;
import pairly.domain.Marker;
import pairly.domain.Role;

class ConsoleViewTest {
    // System.out을 가로채야하기 때문에 다음과 같이 설정함
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final String EOL = System.lineSeparator();

    private ConsoleView consoleView;
    private AtomicReference<ClientState> clientStateHolder;
    private AtomicReference<EditorState> editorStateHolder;

    @BeforeEach
    void setUp() {
        // System.out을 가로챔
        System.setOut(new PrintStream(outContent));

        // 홀더 생성 및 주입
        clientStateHolder = new AtomicReference<>();
        editorStateHolder = new AtomicReference<>();

        consoleView = new ConsoleView(clientStateHolder, editorStateHolder);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("redraw()는 상태를 올바르게 콘솔에 출력한다")
    void shouldRenderCorrectState() {
        // given
        ClientState testClientState = new ClientState(Role.CODE_WRITER, "04:30", "테스트 상태 메시지");
        EditorState testEditorState = new EditorState();
        testEditorState.syncCode(List.of("System.out.prinlnt();", "break;"));
        testEditorState.addMarker(1, new Marker("오타가 발생했어요"));

        clientStateHolder.set(testClientState);
        editorStateHolder.set(testEditorState);

        // when
        consoleView.redraw();

        // then
        String actualOutput = outContent.toString().replaceAll(EOL, "\n");
        String normalizedOutput = actualOutput.replaceAll("\\s+", ""); // 테스트를 위한 노멀라이즈

        assertThat(normalizedOutput).contains("[ROLE:CODE_WRITER|TIMELEFT:04:30]");
        assertThat(normalizedOutput).contains("1:System.out.prinlnt();");
        assertThat(normalizedOutput).contains("2:break;");
        String expectedMarkerText = "\"⭐️오타가 발생했어요\"";
        String normalizedExpectedMarker = expectedMarkerText.replaceAll("\\s+", "");
        assertThat(normalizedOutput).contains(normalizedExpectedMarker);
        assertThat(normalizedOutput).contains("3:~");
        assertThat(normalizedOutput).contains("[STATUS]테스트상태메시지");
        assertThat(normalizedOutput).contains(">");
    }
}