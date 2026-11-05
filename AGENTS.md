# Release flow

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
GitHub release titled `1.1.0` with tag `v1.1.0`, `coding-agent-toolkit-1.1.0.zip`, and
`SHA256SUMS` attached. Review the draft under **GitHub → Releases** and publish
it when ready. Existing tags cannot be reused. Packaging and release creation
only run when you start this workflow manually.
