package dev.nickl0s.opencode;

import com.intellij.diff.DiffContext;
import com.intellij.diff.tools.util.DiffDataKeys;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.vcs.changes.actions.diff.lst.LocalChangeListDiffRequest;
import com.intellij.openapi.vcs.changes.actions.diff.lst.LocalChangeListDiffTool;
import com.intellij.openapi.vcs.ex.ExclusionState;
import com.intellij.openapi.vcs.ex.PartialLocalLineStatusTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ToggleFileInclusionInCommitAction extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Recheck the active diff because the file or inclusion state may have changed since update().
        Target target = findTarget(event);
        if (target == null) return;

        // Match the whole-file checkbox. A mixed state becomes fully included.
        target.tracker().setExcludedFromCommit(target.changelistId(), target.state() == ExclusionState.ALL_INCLUDED);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(findTarget(event) != null);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    private static @Nullable Target findTarget(AnActionEvent event) {
        // Use the active diff request so either pane targets the same file and changelist.
        DiffContext context = event.getData(DiffDataKeys.DIFF_CONTEXT);
        if (context == null
                || !Boolean.TRUE.equals(context.getUserData(LocalChangeListDiffTool.ALLOW_EXCLUDE_FROM_COMMIT))
                || !(event.getData(DiffDataKeys.DIFF_REQUEST) instanceof LocalChangeListDiffRequest request)) {
            return null;
        }

        // Enable the action only while IntelliJ has a valid inclusion state for this changelist entry.
        if (!(request.getLineStatusTracker() instanceof PartialLocalLineStatusTracker tracker) || !tracker.isValid()) {
            return null;
        }
        String changelistId = request.getChangelistId();
        ExclusionState state = tracker.getExcludedFromCommitState(changelistId);
        return state == ExclusionState.NO_CHANGES ? null : new Target(tracker, changelistId, state);
    }

    private record Target(PartialLocalLineStatusTracker tracker, String changelistId, ExclusionState state) {}
}
