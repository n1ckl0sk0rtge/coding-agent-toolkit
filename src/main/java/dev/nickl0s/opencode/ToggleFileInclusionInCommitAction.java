package dev.nickl0s.opencode;

import com.intellij.diff.DiffContext;
import com.intellij.diff.requests.ContentDiffRequest;
import com.intellij.diff.tools.util.DiffDataKeys;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangesViewManager;
import com.intellij.openapi.vcs.changes.ChangesViewPanel;
import com.intellij.openapi.vcs.changes.actions.diff.ChangeDiffRequestProducer;
import com.intellij.openapi.vcs.changes.actions.diff.lst.LocalChangeListDiffRequest;
import com.intellij.openapi.vcs.changes.actions.diff.lst.LocalChangeListDiffTool;
import com.intellij.openapi.vcs.changes.ui.ChangesListView;
import com.intellij.openapi.vcs.ex.ExclusionState;
import com.intellij.openapi.vcs.ex.PartialLocalLineStatusTracker;
import com.intellij.util.ui.ThreeStateCheckBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ToggleFileInclusionInCommitAction extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Recheck the active diff because the file or inclusion state may have changed since update().
        Runnable toggle = findToggle(event);
        if (toggle != null) toggle.run();
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(findToggle(event) != null);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    private static @Nullable Runnable findToggle(AnActionEvent event) {
        // Use the active diff request so either pane targets the same file and changelist.
        DiffContext context = event.getData(DiffDataKeys.DIFF_CONTEXT);
        if (context == null
                || !Boolean.TRUE.equals(context.getUserData(LocalChangeListDiffTool.ALLOW_EXCLUDE_FROM_COMMIT))
                || !(event.getData(DiffDataKeys.DIFF_REQUEST) instanceof ContentDiffRequest request)) {
            return null;
        }

        // Keep using the line tracker when the diff supports partial inclusion.
        if (request instanceof LocalChangeListDiffRequest localRequest) {
            return findTrackedFileToggle(localRequest);
        }

        // Added files have a one-sided diff and use the Commit tree's whole-file inclusion model.
        return findAddedFileToggle(context.getProject(), request.getUserData(ChangeDiffRequestProducer.CHANGE_KEY));
    }

    private static @Nullable Runnable findTrackedFileToggle(LocalChangeListDiffRequest request) {
        // Wait for IntelliJ to compute a valid inclusion state for this changelist entry.
        if (!(request.getLineStatusTracker() instanceof PartialLocalLineStatusTracker tracker) || !tracker.isValid()) {
            return null;
        }
        String changelistId = request.getChangelistId();
        ExclusionState state = tracker.getExcludedFromCommitState(changelistId);
        if (state == ExclusionState.NO_CHANGES) return null;

        // Match the whole-file checkbox. A mixed state becomes fully included.
        return () -> tracker.setExcludedFromCommit(changelistId, state == ExclusionState.ALL_INCLUDED);
    }

    private static @Nullable Runnable findAddedFileToggle(@Nullable Project project, @Nullable Change change) {
        // Only newly added files use the whole-file fallback.
        if (project == null || change == null || change.getType() != Change.Type.NEW) return null;

        // Look up the added file in the Commit tree so a stale diff cannot toggle a different entry.
        if (!(ChangesViewManager.getInstanceEx(project).getOrCreateCommitChangesView()
                .getPanel() instanceof ChangesViewPanel panel)) {
            return null;
        }
        ChangesListView tree = panel.getChangesView();
        if (!tree.isShowCheckboxes() || tree.findNodeInTree(change) == null) {
            return null;
        }

        // Use the same inclusion operations as the Commit checkbox.
        return tree.getInclusionModel().getInclusionState(change) == ThreeStateCheckBox.State.SELECTED
                ? () -> tree.excludeChange(change)
                : () -> tree.includeChange(change);
    }
}
