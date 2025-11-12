package pairly.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EditorStateTest {

    EditorState editorState;

    @BeforeEach
    void setUp() {
        editorState = new EditorState();
    }

    @Nested
    @DisplayName("syncCode 메서드")
    class syncCode {
        @Test
        @DisplayName("새로운 코드로 동기화하면 코드가 갱신된다")
        void syncCodeShouldUpdateCodeLines() {
            // given
            List<String> newCode = List.of("public class Main {}", "void main() {}");

            // when
            editorState.syncCode(newCode);

            // then
            assertThat(editorState.getCodeLines()).isEqualTo(newCode);
        }

        @Test
        @DisplayName("코드를 동기화하면 이전 마커는 모두 사라진다")
        void syncCodeShouldClearAllMarkers() {
            // given
            editorState.addMarker(1, new Marker("이전 코멘트"));

            // when
            List<String> newCode = List.of("public static void main(String[])", "{ System.out.println(); }");
            editorState.syncCode(newCode);

            // then
            assertThat(editorState.getMarks()).isEmpty();
        }
    }

    @Nested
    @DisplayName("addMarker")
    class addMarker {
        @Test
        @DisplayName("새 라인에 마커를 추가하면 정상적으로 조회된다")
        void addMarkerShouldAddMarker() {
            // given
            Marker newMarker = new Marker("새로운 코멘트");

            // when
            editorState.addMarker(10, newMarker);

            // then
            assertThat(editorState.getMarks()).hasSize(1);
            assertThat(editorState.getMarks().get(10)).isEqualTo(newMarker);
            assertThat(editorState.getMarks().get(10).getComment()).isEqualTo("새로운 코멘트");
        }

        @Test
        @DisplayName("기존 라인에 마커를 추가하면 덮어쓴다")
        void addMarkerShouldOverwriteExistingMarker() {
            // given
            Marker oldMarker = new Marker("이전 코멘트");
            editorState.addMarker(3, oldMarker);

            Marker newMarker = new Marker("새로운 코멘트");

            // when
            editorState.addMarker(3, newMarker);

            // then
            assertThat(editorState.getMarks()).hasSize(1);
            assertThat(editorState.getMarks().get(3)).isEqualTo(newMarker);
            assertThat(editorState.getMarks().get(3).getComment()).isEqualTo("새로운 코멘트");
        }
    }
}