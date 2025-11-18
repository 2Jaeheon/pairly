package pairly.client.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pairly.common.exception.InvalidCommandException;
import pairly.domain.command.Command;
import pairly.domain.command.MarkCommand;
import pairly.domain.command.QuitCommand;
import pairly.domain.command.SyncCommand;

class CommandParserTest {
    private CommandParser parser;

    @BeforeEach
    void setUp() {
        parser = new CommandParser();
    }

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {
        @Test
        @DisplayName(":quit 명령어를 QuitCommand로 파싱")
        void parseQuitCommand() {
            // given
            String input = ":quit";

            // when
            Command parsedCommand = parser.parse(input);

            // then
            assertThat(parsedCommand).isInstanceOf(QuitCommand.class);
        }

        @Test
        @DisplayName(":m 명령어를 MarkCommand로 파싱")
        void parseMarkCommand() {
            // given
            String input = ":m 5 \"코멘트입니다.\"";
            MarkCommand expectedCommand = new MarkCommand(5, "코멘트입니다.");

            // when
            Command parsedCommand = parser.parse(input);

            // then
            assertThat(parsedCommand).isInstanceOf(MarkCommand.class);
            assertThat(parsedCommand)
                    .usingRecursiveComparison() // 내부 상태를 비교하는데 사용
                    .isEqualTo(expectedCommand);
        }

        @Test
        @DisplayName(":sync 명령어를 SyncCommand로 파싱")
        void parseSyncCommand(@TempDir Path tempDir) throws IOException {
            // given
            Path testFile = tempDir.resolve("testApp.java");
            List<String> expectedLines = List.of("public class {", "  // 한글 테스트", "}");
            Files.write(testFile, expectedLines, StandardCharsets.UTF_8);
            String input = ":sync" + " " + testFile.toAbsolutePath();
            SyncCommand expectedCommand = new SyncCommand(expectedLines);

            // when
            Command command = parser.parse(input);

            // then
            assertThat(command).isInstanceOf(SyncCommand.class);
            assertThat(command)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedCommand);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {
        @Test
        @DisplayName("null이나 빈 문자열을 입력하면 예외를 던진다")
        void shouldThrowWhenNullOrEmpty() {
            assertThatThrownBy(() -> parser.parse(null))
                    .isInstanceOf(InvalidCommandException.class)
                    .hasMessageContaining("입력이 비어있습니다.");

            assertThatThrownBy(() -> parser.parse("  "))
                    .isInstanceOf(InvalidCommandException.class)
                    .hasMessageContaining("입력이 비어있습니다.");
        }

        @Test
        @DisplayName("':'로 시작하지 않으면 예외를 던진다")
        void shouldStartsWithColon() {
            assertThatThrownBy(() -> parser.parse("hello"))
                    .isInstanceOf(InvalidCommandException.class)
                    .hasMessageContaining("':'로 시작해야 합니다.");
        }

        @Test
        @DisplayName("알 수 없는 명령어를 입력하면 예외를 던진다")
        void shouldCorrectCommand() {
            assertThatThrownBy(() -> parser.parse(":x"))
                    .isInstanceOf(InvalidCommandException.class)
                    .hasMessageContaining("알 수 없는 명령어");
        }

        @Test
        @DisplayName(":sync 명령어의 파일 경로가 존재하지 않으면 예외를 던진다")
        void shouldThrowWhenWrongFileLocation() {
            String input = ":sync /invalid/path/nonExistFile.java";

            assertThatThrownBy(() -> parser.parse(input))
                    .isInstanceOf(InvalidCommandException.class)
                    .hasMessageContaining("파일을 찾을 수 없습니다");
        }
    }
}