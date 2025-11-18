package pairly.domain.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pairly.domain.EditorState;
import pairly.domain.Marker;

class CommandTest {
    EditorState editorState;

    @BeforeEach
    void setUp() {
        editorState = new EditorState();
    }

    @Nested
    @DisplayName("SyncCommand")
    class DescribeSyncCommand {

        @Test
        @DisplayName("execute 호출 시 EditorState의 코드를 갱신하고 마커를 초기화한다")
        void shouldUpdateEditorState() {
            // given
            editorState.addMarker(1, new Marker("이전 마커"));
            List<String> newCode = List.of("public static void main", "(String[] args)");
            Command syncCommand = new SyncCommand(newCode);

            // when
            syncCommand.execute(editorState);

            // then
            assertThat(editorState.getCodeLines()).isEqualTo(newCode);
            assertThat(editorState.getMarks()).isEmpty();
        }
    }

    @Nested
    @DisplayName("MarkCommand")
    class DescribeMarkCommand {
        @Test
        @DisplayName("execute 호출 시 EditorState에 마커를 추가한다")
        void ShouldAddMarker() {
            // given
            int lineNumber = 3;
            String comment = "테스트 코멘트";
            Command markCommand = new MarkCommand(lineNumber, comment);

            // when
            markCommand.execute(editorState);

            // then
            Map<Integer, Marker> marks = editorState.getMarks();
            assertThat(marks).hasSize(1);
            assertThat(marks).containsKey(lineNumber);
            assertThat(marks.get(lineNumber))
                    .extracting(Marker::getComment)
                    .isEqualTo(comment);
        }
    }

    @Nested
    @DisplayName("QuitCommand")
    class Describe_QuitCommand {

        @Test
        @DisplayName("execute 호출 시 EditorState를 변경하지 않는다")
        void execute_should_not_change_state() {
            // given
            editorState.syncCode(List.of("line 1"));
            editorState.addMarker(1, new Marker("marker"));

            List<String> linesBefore = editorState.getCodeLines();
            Map<Integer, Marker> marksBefore = editorState.getMarks();

            QuitCommand quitCommand = new QuitCommand();

            // when
            quitCommand.execute(editorState);

            // then
            assertThat(editorState.getCodeLines()).isEqualTo(linesBefore);
            assertThat(editorState.getMarks()).isEqualTo(marksBefore);
        }
    }
}