package pairly.domain.command;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import pairly.common.exception.InvalidCommandException;
import pairly.common.message.ClientMessage;
import pairly.common.message.MessageType;

public class CommandFactory {
    private static final String MARK_DELIMITER = "|";

    public Command create(ClientMessage message) {
        MessageType type = message.getType();
        String payload = message.getMessage();

        switch (type) {
            case SYNC:
                List<String> codeLines = Arrays.asList(payload.split("\n"));
                return new SyncCommand(codeLines);

            case MARK:
                try {
                    String[] parts = payload.split(Pattern.quote(MARK_DELIMITER), 2);
                    if (parts.length < 2) {
                        throw new InvalidCommandException(":m 명령어 페이로드가 잘못되었습니다");
                    }
                    int lineNumber = Integer.parseInt(parts[0]);
                    String comment = parts[1];
                    return new MarkCommand(lineNumber, comment);
                } catch (NumberFormatException e) {
                    throw new InvalidCommandException(":m 명령어의 줄 번호가 유효하지 않습니다.");
                }

            case QUIT:
                return new QuitCommand();

            default:
                throw new InvalidCommandException("알 수 없거나 처리할 수 없는 명령어 타입입니다: ");
        }
    }
}
