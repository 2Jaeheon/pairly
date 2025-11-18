package pairly.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pairly.domain.Role;

class ClientStateTest {
    @Test
    @DisplayName("initialState는 올바른 초기값을 반환한다")
    void initialStateShouldReturnCorrectValues() {
        // when
        ClientState state = ClientState.initialState();

        // then
        assertThat(state.role()).isNull();
        assertThat(state.leftTime()).isEqualTo("00:00");
        assertThat(state.statusMessage()).isEqualTo("서버에 연결 중...");
    }

    @Nested
    @DisplayName("getRoleDisplayName() 메서드")
    class GetRoleDisplayNameTest {
        @Test
        @DisplayName("역할이 Null일 때 연결중을 반환한다")
        void ShouldReturnIsConnectingWhenRoleIsNull() {
            // given
            ClientState state = ClientState.initialState();

            // when
            String name = state.getRoleDisplayName();
            assertThat(name).isEqualTo("연결 중...");
        }

        @Test
        @DisplayName("역할이 코드 작성자일 때 CODE_WRITER를 반환한다")
        void shouldReturnCODE_WRITERWhenRoleIsCodeWriter() {
            // given
            ClientState state = new ClientState(Role.CODE_WRITER, "05:00", "");

            // when
            String roleName = state.getRoleDisplayName();

            // then
            assertThat(roleName).isEqualTo("CODE_WRITER");
        }

        @Test
        @DisplayName("역할이 코드 리뷰어일 때 CODE_REVIEWER를 반환한다")
        void shouldReturnCODE_REVIEWERWhenRoleIsCodeReviewer() {
            // given
            ClientState state = new ClientState(Role.CODE_REVIEWER, "05:00", "");

            // when
            String roleName = state.getRoleDisplayName();

            // then
            assertThat(roleName).isEqualTo("CODE_REVIEWER");
        }
    }
}