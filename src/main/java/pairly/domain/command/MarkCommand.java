package pairly.domain.command;

import pairly.domain.EditorState;
import pairly.domain.Marker;

/**
 * :m 명령어가 해야하는 행동을 구현한 객체
 */
public class MarkCommand implements Command {
    private final int lineNumber;
    private final String comment;

    public MarkCommand(int lineNumber, String comment) {
        this.lineNumber = lineNumber;
        this.comment = comment;
    }

    @Override
    public void execute(EditorState editorState) {
        Marker marker = new Marker(comment);
        editorState.addMarker(lineNumber, marker);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getComment() {
        return comment;
    }
}
