# Coding Agent Toolkit

A local JetBrains plugin for working with coding agents:

- Launch a configurable coding harness in an editor terminal.
- Copy selected code as a project-relative file and line reference.

## Code references

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

Install the ZIP from `build/distributions/` using **Settings →
Plugins → ⚙ → Install Plugin from Disk…**. Restart the IDE when prompted.

Previously named **OpenCode Selection Reference**. The plugin and action IDs are
retained so existing installations and custom keybindings continue to work.

## Release flow

The version is stored in `gradle.properties`. `main` uses `2.0.0-snapshot` for
ongoing development. A release branch uses its final version, for example
`release/1.1.0` uses `version=1.1.0`.

The **CI** workflow runs all tests and plugin configuration checks on pushes to
`main` and `release/*`, including the first push that creates a release branch.
It also runs on pull requests targeting those branches. Test reports are saved
as workflow artifacts.

To prepare a new release branch:

```sh
git switch main
git pull --ff-only
git switch -c release/1.2.0
# Set version=1.2.0 in gradle.properties.
git add gradle.properties
git commit -m "Prepare release 1.2.0"
git push -u origin release/1.2.0
```

To build the release package, open **GitHub → Actions → Release → Run workflow**
and select the release branch. You can also start it with GitHub CLI:

```sh
gh workflow run release.yml --ref release/1.1.0
```

The manual workflow checks that the branch and project versions match, reruns
all tests, and builds the installable plugin ZIP. It then creates a **draft**
GitHub release named `v1.1.0` with `coding-agent-toolkit-1.1.0.zip` and
`SHA256SUMS` attached. Review the draft under **GitHub → Releases** and publish
it when ready. Existing tags cannot be reused. Packaging and release creation
only run when you start this workflow manually.
