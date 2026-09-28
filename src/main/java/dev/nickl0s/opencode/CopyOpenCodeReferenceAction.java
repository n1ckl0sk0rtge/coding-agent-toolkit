package dev.nickl0s.opencode;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

/** Copies an editor selection in OpenCode's @path#Lstart-end reference syntax. */
public final class CopyOpenCodeReferenceAction extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        Project project = event.getProject();
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);

        if (editor == null || project == null || file == null) return;

        SelectionModel selection = editor.getSelectionModel();
        if (!selection.hasSelection()) return;

        int startLine = editor.getDocument().getLineNumber(selection.getSelectionStart()) + 1;
        // The end offset is exclusive. Subtracting one avoids claiming the next
        // line when a selection ends exactly at its first character.
        int endLine = editor.getDocument().getLineNumber(selection.getSelectionEnd() - 1) + 1;

        String reference = OpenCodeReference.format(project, file, startLine, endLine);
        CopyPasteManager.getInstance().setContents(new StringSelection(reference));
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
        boolean enabled = editor != null
                && file != null
                && FileDocumentManager.getInstance().getFile(editor.getDocument()) != null
                && editor.getSelectionModel().hasSelection();
        event.getPresentation().setEnabledAndVisible(enabled);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}

