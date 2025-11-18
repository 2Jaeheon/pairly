package pairly.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AnsiConsoleTest {

    private static final String RESET = "\033[0m";
    private static final String YELLOW = "\033[0;33m";
    private static final String CYAN = "\033[0;36m";
    private static final String GREEN = "\033[0;32m";
    private static final String RED = "\033[0;31m";
    private static final String INPUT_TEXT = "Test";

    @Test
    @DisplayName("colorYellow는 텍스트에 노란색을 적용한다")
    void colorYellowShouldWrapTextWithYellow() {
        // given
        String expected = YELLOW + INPUT_TEXT + RESET;

        // when
        String actual = AnsiConsole.colorYellow(INPUT_TEXT);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("colorCyan은 텍스트에 청록색을 적용한다")
    void colorCyanShouldWrapTextWithCyan() {
        // given
        String expected = CYAN + INPUT_TEXT + RESET;

        // when
        String actual = AnsiConsole.colorCyan(INPUT_TEXT);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("colorGreen은 텍스트에 초록색을 적용한다")
    void colorGreenShouldWrapTextWithGreen() {
        // given
        String expected = GREEN + INPUT_TEXT + RESET;

        // when
        String actual = AnsiConsole.colorGreen(INPUT_TEXT);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("colorRed는 텍스트에 빨간색을 적용한다")
    void colorRedShouldWrapTextWithRed() {
        // given
        String expected = RED + INPUT_TEXT + RESET;

        // when
        String actual = AnsiConsole.colorRed(INPUT_TEXT);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}