package pairly.client.parser;

import static pairly.common.constant.ErrorMessage.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pairly.common.exception.InvalidCommandException;
import pairly.domain.command.Command;
import pairly.domain.command.MarkCommand;
import pairly.domain.command.QuitCommand;
import pairly.domain.command.SyncCommand;

public class CommandParser {
    // 커맨드를 파싱하기 위한 정규식 표현
    private static final Pattern MARK_PATTERN = Pattern.compile("^:m\\s+(\\d+)\\s+\"(.*)\"$");
    private static final Pattern SYNC_PATTERN = Pattern.compile("^:sync\\s+(.+)$");

    public Command parse(String input) {
        if (input == null || input.isBlank()) {
            throw new InvalidCommandException(EMPTY_INPUT);
        }

        String trimmedInput = input.trim();

        if (!trimmedInput.startsWith(":")) {
            throw new InvalidCommandException(COMMAND_PREFIX);
        }

        // :quit 명령어
        if (trimmedInput.equals(":quit")) {
            return new QuitCommand();
        }

        // :sync 명령어
        Matcher syncMatcher = SYNC_PATTERN.matcher(trimmedInput);
        if (syncMatcher.matches()) {
            String filePath = syncMatcher.group(1).trim();
            return parseSyncCommand(filePath);
        }

        // :m 명령어
        Matcher markMatcher = MARK_PATTERN.matcher(trimmedInput);
        if (markMatcher.matches()) {
            int lineNumber = Integer.parseInt(markMatcher.group(1));
            String comment = markMatcher.group(2);
            return new MarkCommand(lineNumber, comment);
        }

        throw new InvalidCommandException(INVALID_INPUT);
    }

    private SyncCommand parseSyncCommand(String filePathStr) {
        try {
            // 절대 경로를 통해 안전성을 확보
            Path filePath = Paths.get(filePathStr).toAbsolutePath();

            if (!Files.exists(filePath)) {
                throw new InvalidCommandException(FILE_NOT_FOUND + filePathStr);
            }
            if (!Files.isReadable(filePath)) {
                throw new InvalidCommandException(FILE_NOT_READABLE + filePathStr);
            }

            List<String> codeLines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            return new SyncCommand(codeLines);

        } catch (IOException e) {
            throw new InvalidCommandException(IO_ERROR, e);
        } catch (InvalidPathException e) {
            throw new InvalidCommandException(INVALID_PATH + filePathStr, e);
        }
    }
}
