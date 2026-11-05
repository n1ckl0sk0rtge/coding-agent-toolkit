package dev.nickl0s.opencode;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@Service(Service.Level.APP)
@State(name = "CodingHarnessSettings", storages = @Storage("codingHarness.xml"))
public final class CodingHarnessSettings implements PersistentStateComponent<CodingHarnessSettings.State> {
    static final String DEFAULT_COMMAND = "codex";

    private State state = new State();

    public static CodingHarnessSettings getInstance() {
        return ApplicationManager.getApplication().getService(CodingHarnessSettings.class);
    }

    public static final class State {
        public String command = DEFAULT_COMMAND;
    }

    @Override
    public @NotNull State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        // Keep malformed or older settings from producing an empty terminal command.
        this.state = new State();
        this.state.command = isValidCommand(state.command) ? state.command.strip() : DEFAULT_COMMAND;
    }

    public String getCommand() {
        return state.command;
    }

    public void setCommand(String command) {
        // Validate before storing commands from settings or other callers.
        if (!isValidCommand(command)) {
            throw new IllegalArgumentException("Enter a single-line harness command, for example codex or opencode.");
        }

        state.command = command.strip();
    }

    static boolean isValidCommand(String command) {
        return command != null && !command.isBlank()
                && command.chars().noneMatch(character -> Character.isISOControl(character)
                || character == '\u2028' || character == '\u2029');
    }
}
