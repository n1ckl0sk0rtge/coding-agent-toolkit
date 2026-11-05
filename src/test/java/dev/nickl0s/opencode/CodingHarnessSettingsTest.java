package dev.nickl0s.opencode;

import com.intellij.util.xmlb.XmlSerializer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class CodingHarnessSettingsTest {
    @Test
    public void defaultsToCodex() {
        assertEquals("codex", new CodingHarnessSettings().getCommand());
    }

    @Test
    public void preservesQuotedArgumentsThroughPersistence() {
        // Save a command containing a path with spaces and harness arguments.
        CodingHarnessSettings settings = new CodingHarnessSettings();
        settings.setCommand("  \"/path with spaces/opencode\" --model provider/model  ");
        var serialized = XmlSerializer.serialize(settings.getState());

        // Restore the persisted state without changing shell quoting or arguments.
        CodingHarnessSettings restored = new CodingHarnessSettings();
        restored.loadState(XmlSerializer.deserialize(serialized, CodingHarnessSettings.State.class));
        assertEquals("\"/path with spaces/opencode\" --model provider/model", restored.getCommand());
    }

    @Test
    public void rejectsBlankAndControlInputWithoutReplacingCommand() {
        // Keep the last valid command when an attempted update is invalid.
        CodingHarnessSettings settings = new CodingHarnessSettings();
        settings.setCommand("opencode");

        for (String command : new String[]{null, "", "  ", "codex\nexit", "codex\r", "codex\u0000", "codex\u001b", "codex\u2028exit"}) {
            assertThrows(IllegalArgumentException.class, () -> settings.setCommand(command));
            assertEquals("opencode", settings.getCommand());
        }
    }

    @Test
    public void recoversFromInvalidSavedSettings() {
        // Treat missing or damaged saved commands as the default harness.
        CodingHarnessSettings settings = new CodingHarnessSettings();
        for (String command : new String[]{null, "", "  ", "codex\nexit"}) {
            CodingHarnessSettings.State state = new CodingHarnessSettings.State();
            state.command = command;
            settings.loadState(state);
            assertEquals("codex", settings.getCommand());
        }
    }
}
