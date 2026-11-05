package dev.nickl0s.opencode;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;

public final class CodingHarnessConfigurable implements Configurable {
    private JBTextField commandField;

    @Override
    public @Nls String getDisplayName() {
        return "Coding Harness";
    }

    @Override
    public @Nullable JComponent createComponent() {
        // Use the IDE's terminal shell so aliases, PATH, and shell configuration apply.
        commandField = new JBTextField(CodingHarnessSettings.getInstance().getCommand(), 40);

        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Harness command:", commandField)
                .addComponentToRightColumn(new JBLabel(
                        "Examples: codex, opencode, claude, or a command with arguments."))
                .addComponentToRightColumn(new JBLabel(
                        "Runs in a new editor terminal at the project root using your terminal shell."))
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return commandField;
    }

    @Override
    public boolean isModified() {
        return commandField != null
                && !commandField.getText().strip().equals(CodingHarnessSettings.getInstance().getCommand());
    }

    @Override
    public void apply() throws ConfigurationException {
        // Reject invalid input before replacing the saved command.
        if (commandField == null) return;
        String command = commandField.getText();
        if (!CodingHarnessSettings.isValidCommand(command)) {
            throw new ConfigurationException("Enter a single-line harness command, for example codex or opencode.");
        }

        CodingHarnessSettings.getInstance().setCommand(command);
    }

    @Override
    public void reset() {
        if (commandField != null) commandField.setText(CodingHarnessSettings.getInstance().getCommand());
    }

    @Override
    public void disposeUIResources() {
        commandField = null;
    }
}
