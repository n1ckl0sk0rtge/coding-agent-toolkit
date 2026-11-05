package dev.nickl0s.opencode;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Disposer;
import com.intellij.terminal.frontend.editor.TerminalViewVirtualFile;
import com.intellij.terminal.frontend.toolwindow.TerminalToolWindowTab;
import com.intellij.terminal.frontend.toolwindow.TerminalToolWindowTabsManager;
import org.jetbrains.annotations.NotNull;

public final class OpenCodingHarnessAction extends AnAction implements DumbAware {
    static final String ACTION_ID = "dev.nickl0s.opencode.OpenCodingHarness";
    private static final Logger LOG = Logger.getInstance(OpenCodingHarnessAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Resolve the command and working directory from the active project.
        Project project = event.getProject();
        if (project == null || project.isDisposed() || project.getBasePath() == null) return;
        String command = CodingHarnessSettings.getInstance().getCommand();

        // Create a detached terminal so it opens directly in the editor area.
        TerminalToolWindowTab tab = null;
        boolean opened = false;
        try {
            tab = createTerminalTab(project);

            // Let the native terminal editor own the session and its disposal.
            TerminalViewVirtualFile file = new TerminalViewVirtualFile(tab);
            if (FileEditorManager.getInstance(project).openFile(file, true).length == 0) {
                throw new IllegalStateException("The terminal editor could not be opened.");
            }
            opened = true;

            // The terminal API queues input until the shell session is connected.
            tab.getView().createSendTextBuilder().shouldExecute().send(command);
        } catch (ProcessCanceledException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            LOG.warn("Could not open the coding harness terminal", exception);
            Messages.showErrorDialog(project,
                    "Could not start the coding harness terminal. Check that the Terminal plugin is enabled.\n"
                            + "Details: " + exception.getMessage(),
                    "Coding Harness");
        } finally {
            // Failed editor creation must not leave a shell running without a tab.
            if (!opened && tab != null) Disposer.dispose(tab.getContent());
        }
    }

    static TerminalToolWindowTab createTerminalTab(Project project) {
        return TerminalToolWindowTabsManager.getInstance(project).createTabBuilder()
                .workingDirectory(project.getBasePath())
                .tabName("Coding Harness")
                .userDefinedName(true)
                .requestFocus(false)
                .shouldAddToToolWindow(false)
                .restoreOnProjectReopen(false)
                .closeOnProcessTermination(false)
                .createTab();
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        event.getPresentation().setEnabled(project != null && !project.isDisposed() && project.getBasePath() != null);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
