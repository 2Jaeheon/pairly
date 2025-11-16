package pairly.domain.command;

import java.util.List;
import pairly.domain.EditorState;

public class SyncCommand implements Command {
    public SyncCommand(List<String> codeLines) {
        this.codeLines = codeLines;
    }

    private final List<String> codeLines;

    @Override
    public void execute(EditorState editorState) {
        // 이 객체의 책임은 'EditorState의 코드를 동기화'하는 것.
        editorState.syncCode(codeLines);
    }

    public List<String> getCodeLines() {
        return codeLines;
    }
}
