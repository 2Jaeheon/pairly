package pairly.common.util;

public class AnsiConsole {
    private static final String RESET = "\033[0m";

    // 텍스트 색상
    private static final String YELLOW = "\033[0;33m";
    private static final String CYAN = "\033[0;36m";
    private static final String GREEN = "\033[0;32m";
    private static final String RED = "\033[0;31m";

    public static final String SAVE_CURSOR = "\033[s";
    public static final String RESTORE_CURSOR = "\033[u";
    public static final String CLEAR_LINE = "\033[K";

    // 커서를 홈으로 이동한 뒤, 화면을 지움
    private static final String CLEAR_SCREEN = "\033[H\033[2J";

    private AnsiConsole() {
    }

    public static String moveTo(int row, int col) {
        return "\033[" + row + ";" + col + "H";
    }

    public static void clearScreen() {
        System.out.print(CLEAR_SCREEN);
        System.out.flush();
    }

    public static void printAndFlush(String text) {
        System.out.print(text);
        System.out.flush();
    }

    public static String colorYellow(String text) {
        return YELLOW + text + RESET;
    }

    public static String colorCyan(String text) {
        return CYAN + text + RESET;
    }

    public static String colorGreen(String text) {
        return GREEN + text + RESET;
    }

    public static String colorRed(String text) {
        return RED + text + RESET;
    }
}
