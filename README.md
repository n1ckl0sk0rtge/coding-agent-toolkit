# OpenCode Selection Reference

A local JetBrains plugin that opens coding harnesses in editor terminals and
copies editor selections to the clipboard in OpenCode reference syntax:

```text
@src/main.js#L10-25
```

The path is relative to the project root. One selected line is represented as a
range, for example `@src/A.java#L12-12`.

## Coding harness terminal

Press **⌘⌥⇧H** on macOS or **Ctrl+Alt+Shift+H** on Windows/Linux to open a new
terminal tab in the editor area and start `codex` at the project root. Each press
starts a separate session. The action is also available under **Tools → Open
Coding Harness in Editor** and in the editor context menu.

Change the command under **Settings → Tools → Coding Harness**. For example,
use `opencode`, `claude`, or a harness command with arguments. The command runs
through your configured IDE terminal shell, so the harness must already be
installed and available there. If the shell cannot find it, its error remains
visible in the terminal so you can fix the command or PATH.

To change the shortcut, search for **Open Coding Harness in Editor** under
**Settings → Keymap**. Close the editor tab to end its terminal session. The
bundled **Terminal** plugin must be enabled.

## Build and install

Targets IntelliJ IDEA 2026.3 (platform build 263). Build with JDK 25.

```sh
./gradlew buildPlugin
```

Install the resulting ZIP from `build/distributions/` using **Settings → Plugins
→ ⚙ → Install Plugin from Disk…**. Restart the IDE when prompted.

The action appears in the editor context menu as **Copy OpenCode Reference**.
To add it to the selection toolbar, select code, open the floating toolbar's
**…** menu, choose **Customize Toolbar**, and add **Copy OpenCode Reference**
under **Floating Code Toolbar**.
