package pairly.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MarkerTest {
    @DisplayName("마커가 null일 때 예외를 발생한다.")
    @Test
    void shouldThrowWhenMarkerIsNull() {
        // when & then
        assertThatThrownBy(() -> new Marker(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("코멘트는 null일 수 없습니다.");
    }

    @DisplayName("마커 생성시 유효한 코멘트는 정확히 반환된다.")
    @Test
    void shouldReturnWhenRightComment() {
        // given
        Marker marker = new Marker("marker test");

        // when
        String comment = marker.getComment();

        // then
        assertThat(comment).isEqualTo("marker test");
    }
}