package dev.nickl0s.opencode;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.terminal.frontend.editor.TerminalViewFileEditorProvider;
import com.intellij.terminal.frontend.editor.TerminalViewVirtualFile;
import com.intellij.terminal.frontend.toolwindow.TerminalToolWindowTab;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class OpenCodingHarnessActionTest extends BasePlatformTestCase {
    public void testCreatesSeparateNativeTerminalEditorsAndDisposesTheirSessions() {
        // Resolve the plugin action and build two detached terminal sessions.
        var action = ActionManager.getInstance().getAction(OpenCodingHarnessAction.ACTION_ID);
        assertInstanceOf(action, OpenCodingHarnessAction.class);
        assertNotNull(getProject().getBasePath());
        TerminalToolWindowTab first = OpenCodingHarnessAction.createTerminalTab(getProject());
        TerminalToolWindowTab second = null;

        try {
            second = OpenCodingHarnessAction.createTerminalTab(getProject());
            assertNotSame(first.getView(), second.getView());

            // Use the native provider directly: the headless editor manager supports text files only.
            var provider = new TerminalViewFileEditorProvider();
            for (var tab : new TerminalToolWindowTab[]{first, second}) {
                assertNull(tab.getContent().getManager());
                assertEquals(getProject().getBasePath(), tab.getProcessOptions().getWorkingDirectory());
                assertFalse(tab.getRestoreOnProjectReopen());
                var file = new TerminalViewVirtualFile(tab);
                assertTrue(provider.accept(getProject(), file));
                var editor = provider.createEditor(getProject(), file);

                // Disposing the editor must release the session owned by its content.
                assertSame(tab.getContent().getComponent(), editor.getComponent());
                Disposer.dispose(editor);
                assertTrue(Disposer.isDisposed(tab.getContent()));
            }
        } finally {
            // Dispose any sessions left behind by an unsuccessful assertion.
            if (!Disposer.isDisposed(first.getContent())) Disposer.dispose(first.getContent());
            if (second != null && !Disposer.isDisposed(second.getContent())) Disposer.dispose(second.getContent());
        }
    }
}
