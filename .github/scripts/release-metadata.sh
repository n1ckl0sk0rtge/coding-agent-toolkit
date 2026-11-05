#!/usr/bin/env bash
set -euo pipefail

# Release builds must come from an explicitly versioned release branch.
if [[ "${GITHUB_REF_TYPE:-}" != branch || ! "${GITHUB_REF_NAME:-}" =~ ^release/([0-9]+\.[0-9]+\.[0-9]+)$ ]]; then
  echo '::error::Select a release/X.Y.Z branch when starting this workflow.' >&2
  exit 1
fi
release_version="${BASH_REMATCH[1]}"

# Resolve the actual build version so snapshots or mismatched branches cannot ship.
project_version="$(./gradlew --quiet --console=plain properties --property version | sed -n 's/^version: //p')"
if [[ "$project_version" != "$release_version" ]]; then
  echo "::error::Branch version ${release_version} does not match project version ${project_version}." >&2
  exit 1
fi

# Keep existing release tags immutable, including when a workflow is rerun.
release_tag="v${release_version}"
if git show-ref --verify --quiet "refs/tags/${release_tag}"; then
  echo "::error::Tag ${release_tag} already exists. Use a new release version." >&2
  exit 1
fi

# Share the validated metadata with the packaging and GitHub release steps.
{
  echo "version=${release_version}"
  echo "tag=${release_tag}"
} >> "$GITHUB_OUTPUT"
