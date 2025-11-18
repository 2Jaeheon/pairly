package pairly.domain.command;

import pairly.domain.EditorState;

public interface Command {
    void execute(EditorState editorState);
}
