package pairly.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pairly.common.exception.InvalidCommandException;
import pairly.domain.Role;
import pairly.domain.command.Command;
import pairly.domain.command.MarkCommand;
import pairly.domain.command.QuitCommand;
import pairly.domain.command.SyncCommand;

class RoleManagerTest {
    RoleManager roleManager;

    @BeforeEach
    void setUp() {
        roleManager = new RoleManager();
    }

    @Nested
    @DisplayName("역할 교환 테스트")
    class SwapRoleTests {
        @Test
        @DisplayName("코드 작성자에서 코드 리뷰어로 역할 전환")
        void shouldChangeWriterToReviewer() {
            // given
            Role codeWriter = Role.CODE_WRITER;
            Role codeReviewer = Role.CODE_REVIEWER;

            // when
            Role changedRole = roleManager.swapRole(codeWriter);

            // then
            assertThat(changedRole).isEqualTo(codeReviewer);
        }

        @Test
        @DisplayName("코드 리뷰어에서 코드 작성자로 역할 전환")
        void shouldChangeReviewerToWriter() {
            // given
            Role codeWriter = Role.CODE_WRITER;
            Role codeReviewer = Role.CODE_REVIEWER;

            // when
            Role changedRole = roleManager.swapRole(codeReviewer);

            // then
            assertThat(changedRole).isEqualTo(codeWriter);
        }
    }

    @Nested
    @DisplayName("역할 검증 테스트")
    class ValidateRoleTests {
        @Test
        @DisplayName("코드 작성자는 :sync 커멘드를 사용할 수 있다")
        void shouldWriterUsableSyncCommand() {
            // given
            Command command = new SyncCommand(List.of("System.out.println(", "hello World!", ")"));
            Role codeWriter = Role.CODE_WRITER;

            // when & then
            assertDoesNotThrow(() -> roleManager.validate(codeWriter, command));
        }

        @Test
        @DisplayName("코드 리뷰어는 :m 커멘드를 사용할 수 있다")
        void shouldReviewerUsableMarkCommand() {
            // given
            Command command = new MarkCommand(1, "hello World!");
            Role codeReviewer = Role.CODE_REVIEWER;

            // when & then
            assertDoesNotThrow(() -> roleManager.validate(codeReviewer, command));
        }

        @Test
        @DisplayName("코드 작성자 및 리뷰어는 :quit 커멘드를 사용할 수 있다")
        void shouldWriterAndReviewerUsableQuitCommand() {
            // given
            Command command = new QuitCommand();
            Role codeWriter = Role.CODE_WRITER;
            Role codeReviewer = Role.CODE_REVIEWER;

            // when & then
            assertDoesNotThrow(() -> {
                roleManager.validate(codeWriter, command);
                roleManager.validate(codeReviewer, command);
            });
        }

        @Test
        @DisplayName("코드 작성자는 :m 커멘드를 사용할 수 없다")
        void shouldWriterNotUsableMarkCommand() {
            Command command = new MarkCommand(1, "hello World!");
            Role codeWriter = Role.CODE_WRITER;

            // when
            assertThatThrownBy(() -> roleManager.validate(codeWriter, command))
                    .isInstanceOf(InvalidCommandException.class);
        }

        @Test
        @DisplayName("코드 리뷰어는 :sync 커멘드를 사용할 수 없다")
        void shouldReviewerNotUsableSyncCommand() {
            // given
            Command command = new SyncCommand(List.of("System.out.println(", "hello World!", ")"));
            Role codeReviewer = Role.CODE_REVIEWER;

            // when & then
            assertThatThrownBy(() -> roleManager.validate(codeReviewer, command))
                    .isInstanceOf(InvalidCommandException.class);
        }
    }
}