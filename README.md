# Coding Agent Toolkit

A local JetBrains plugin for copying code references and reviewing changes in
the Commit diff.

Select code and choose **Copy Code Reference** from the editor context menu to
copy a reference to the clipboard:

```text
@src/main.js#L10-25
```

The path is relative to the project root. One selected line is represented as a
range, for example `@src/A.java#L12-12`.

To add the action to the selection toolbar, select code, open the floating
toolbar's **…** menu, choose **Customize Toolbar**, and add **Copy Code Reference**
under **Floating Code Toolbar**.

## Toggle a file's inclusion from the Commit diff

In **Settings → Keymap**, search for **Toggle File Inclusion in Commit** and
assign a shortcut. With either pane of a Commit diff focused, press it to toggle
the whole-file checkbox for the displayed changelist entry. A partially included
file becomes fully included. The action is available in text diffs that have
IntelliJ's whole-file inclusion checkbox.

## Build and install

Targets IntelliJ IDEA 2026.3 (platform build 263). Build with JDK 25.

```sh
./gradlew buildPlugin
```

Install the ZIP from `build/distributions/` using **Settings →
Plugins → ⚙ → Install Plugin from Disk…**. Restart the IDE when prompted.

Previously named **OpenCode Selection Reference**. The plugin ID and **Copy Code
Reference** action ID are retained for existing installations and keybindings.
