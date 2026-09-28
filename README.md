# OpenCode Selection Reference

A local JetBrains plugin that copies an editor selection to the clipboard in
OpenCode reference syntax:

```text
@src/main.js#L10-25
```

The path is relative to the project root. One selected line is represented as a
range, for example `@src/A.java#L12-12`.

## Build and install

```sh
gradle buildPlugin
```

Install the resulting ZIP from `build/distributions/` using **Settings → Plugins
→ ⚙ → Install Plugin from Disk…**. Restart the IDE when prompted.

The action appears in the editor context menu as **Copy OpenCode Reference**.
To add it to the selection toolbar, select code, open the floating toolbar's
**…** menu, choose **Customize Toolbar**, and add **Copy OpenCode Reference**
under **Floating Code Toolbar**.
