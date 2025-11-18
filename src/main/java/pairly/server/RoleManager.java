package pairly.server;

import pairly.common.exception.InvalidCommandException;
import pairly.domain.Role;
import pairly.domain.command.Command;
import pairly.domain.command.MarkCommand;
import pairly.domain.command.SyncCommand;

public class RoleManager {
    static final String CODE_WRITER_NOT_ALLOWED_MARK = "CODE_WRITER는 :m (마킹) 명령어를 사용할 수 없습니다.";
    static final String CODE_REVIEWER_NOT_ALLOWED_SYNC = "CODE_REVIEWER는 :sync (동기화) 명령어를 사용할 수 없습니다.";

    public Role swapRole(Role currentRole) {
        if (currentRole == Role.CODE_REVIEWER) {
            return Role.CODE_WRITER;
        }

        return Role.CODE_REVIEWER;
    }

    public void validate(Role role, Command command) {
        if (role == Role.CODE_WRITER && command instanceof MarkCommand) {
            throw new InvalidCommandException(CODE_WRITER_NOT_ALLOWED_MARK);
        }
        if (role == Role.CODE_REVIEWER && command instanceof SyncCommand) {
            throw new InvalidCommandException(CODE_REVIEWER_NOT_ALLOWED_SYNC);
        }
    }
}
