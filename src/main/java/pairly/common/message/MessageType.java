package pairly.common.message;

public enum MessageType {
    // 클라이언트 -> 서버
    SYNC,
    MARK,
    QUIT,

    // 클라이언트 <- 서버
    SYNC_UPDATE,
    MARK_UPDATE,
    ROLE_SWAP,
    TIMER_TICK,
    SESSION_START,
    SESSION_END,
    ERROR
}
