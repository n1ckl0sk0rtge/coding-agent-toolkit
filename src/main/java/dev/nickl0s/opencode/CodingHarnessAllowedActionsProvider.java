package dev.nickl0s.opencode;

import com.intellij.terminal.frontend.view.TerminalAllowedActionsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class CodingHarnessAllowedActionsProvider implements TerminalAllowedActionsProvider {
    @Override
    public @NotNull List<String> getActionIds() {
        return List.of(OpenCodingHarnessAction.ACTION_ID);
    }
}
