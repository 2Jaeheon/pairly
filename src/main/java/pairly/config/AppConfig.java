package pairly.config;

import pairly.common.util.JsonConverter;
import pairly.domain.EditorState;
import pairly.domain.command.CommandFactory;
import pairly.server.PomodoroTimer;
import pairly.server.RoleManager;
import pairly.server.SessionManager;

public class AppConfig {

    public SessionManager sessionManager() {
        return new SessionManager(
                jsonConverter(),
                pomodoroTimer(),
                roleManager(),
                editorState(),
                commandFactory()
        );
    }

    public JsonConverter jsonConverter() {
        return new JsonConverter();
    }

    public PomodoroTimer pomodoroTimer() {
        return new PomodoroTimer();
    }

    public RoleManager roleManager() {
        return new RoleManager();
    }

    public EditorState editorState() {
        return new EditorState();
    }

    public CommandFactory commandFactory() {
        return new CommandFactory();
    }
}
