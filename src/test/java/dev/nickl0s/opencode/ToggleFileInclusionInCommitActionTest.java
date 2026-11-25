package dev.nickl0s.opencode;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffContext;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.tools.util.DiffDataKeys;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionUiKind;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListChange;
import com.intellij.openapi.vcs.changes.ChangesViewManager;
import com.intellij.openapi.vcs.changes.ChangesViewPanel;
import com.intellij.openapi.vcs.changes.CurrentContentRevision;
import com.intellij.openapi.vcs.changes.actions.diff.ChangeDiffRequestProducer;
import com.intellij.openapi.vcs.changes.actions.diff.lst.LocalChangeListDiffTool;
import com.intellij.openapi.vcs.changes.ui.ChangesBrowserNode;
import com.intellij.openapi.vcs.changes.ui.ChangesListView;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.vcs.commit.PartialCommitInclusionModel;
import com.intellij.vcsUtil.VcsUtil;

import javax.swing.tree.DefaultTreeModel;

public class ToggleFileInclusionInCommitActionTest extends BasePlatformTestCase {
    public void testTogglesNewFileFromOneSidedDiff() {
        // Build a one-sided diff carrying the changelist entry for the added file.
        String content = "new file\n";
        var file = myFixture.addFileToProject("added.txt", content).getVirtualFile();
        Change change = new ChangeListChange(new Change(null, CurrentContentRevision.create(VcsUtil.getFilePath(file))),
                "changes", "Changes");
        DiffContext context = new TestDiffContext(getProject());
        context.putUserData(LocalChangeListDiffTool.ALLOW_EXCLUDE_FROM_COMMIT, true);
        DiffContentFactory contents = DiffContentFactory.getInstance();
        SimpleDiffRequest request = new SimpleDiffRequest("added.txt", contents.createEmpty(),
                contents.create(content), null, null);
        request.putUserData(ChangeDiffRequestProducer.CHANGE_KEY, change);

        // Show the added file as an included entry in the Commit tree.
        ChangesViewPanel panel = (ChangesViewPanel) ChangesViewManager.getInstanceEx(getProject())
                .getOrCreateCommitChangesView().getPanel();
        ChangesListView tree = panel.getChangesView();
        ChangesBrowserNode<?> root = ChangesBrowserNode.createRoot();
        root.add(ChangesBrowserNode.createChange(getProject(), change));
        tree.setModel(new DefaultTreeModel(root));
        tree.setShowCheckboxes(true);
        PartialCommitInclusionModel model = new PartialCommitInclusionModel(getProject());
        Disposer.register(getTestRootDisposable(), model);
        tree.setInclusionModel(model);
        tree.includeChange(change);

        // Invoke the shortcut action twice and check both states of the Commit checkbox.
        AnAction action = ActionManager.getInstance().getAction("dev.nickl0s.opencode.ToggleFileInclusionInCommit");
        assertNotNull(action);
        var dataContext = SimpleDataContext.builder()
                .add(DiffDataKeys.DIFF_CONTEXT, context)
                .add(DiffDataKeys.DIFF_REQUEST, request)
                .build();
        AnActionEvent event = AnActionEvent.createEvent(action, dataContext, null, "TestCommitDiff", ActionUiKind.NONE, null);
        action.update(event);
        assertTrue(event.getPresentation().isEnabledAndVisible());
        assertTrue(tree.isIncluded(change));
        action.actionPerformed(event);
        assertFalse(tree.isIncluded(change));
        action.actionPerformed(event);
        assertTrue(tree.isIncluded(change));
    }

    private static final class TestDiffContext extends DiffContext {
        private final Project project;

        private TestDiffContext(Project project) {
            this.project = project;
        }

        @Override
        public Project getProject() {
            return project;
        }

        @Override
        public boolean isWindowFocused() {
            return true;
        }

        @Override
        public boolean isFocusedInWindow() {
            return true;
        }

        @Override
        public void requestFocusInWindow() {}
    }
}
