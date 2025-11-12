package pairly.client;

import pairly.domain.Role;

public record ClientState(Role role, String leftTime, String statusMessage) {
    public static ClientState initialState() {
        return new ClientState(null, "00:00", "서버에 연결 중...");
    }

    public String getRoleDisplayName() {
        if (role == null) {
            return "연결 중...";
        }
        if (role == Role.CODE_WRITER) {
            return "CODE_WRITER";
        }
        return "CODE_REVIEWER";
    }
}
