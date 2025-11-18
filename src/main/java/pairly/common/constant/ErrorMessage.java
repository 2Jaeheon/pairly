package pairly.common.constant;

public class ErrorMessage {

    private ErrorMessage() {
    }

    public static final String EMPTY_INPUT = "입력이 비어있습니다.";
    public static final String COMMAND_PREFIX = "명령어는 ':'로 시작해야 합니다.";
    public static final String INVALID_INPUT = "알 수 없는 명령어 형식이거나 잘못된 인수입니다.";

    public static final String FILE_NOT_FOUND = "파일을 찾을 수 없습니다: ";
    public static final String FILE_NOT_READABLE = "파일을 읽을 수 없습니다: ";
    public static final String IO_ERROR = "파일을 읽는 중 I/O 오류가 발생했습니다.";
    public static final String INVALID_PATH = "올바르지 않은 파일 경로 형식입니다: ";
}
