# Repository Instructions

This file provides persistent instructions for AI agents and coding assistants working in the QR Code Scanner repository.

## Required Reading

Before reviewing or modifying this repository:

1. Read `readme.md` for the product overview and documentation index.
2. Read the relevant files under `docs/` and review `changelog.md` when the task affects released or versioned behavior.
3. Consult the shared [ThinApps Android Guidelines](https://github.com/thinapps/android-guidelines) for product, Android, repository, workflow, privacy, Google Play, and release defaults.

If the shared repository is temporarily inaccessible, continue using this file and the app-specific documentation rather than guessing its contents.

## Instruction Precedence

Apply instructions in this order:

1. the user's current request and explicit approvals
2. the mandatory repository policy in this file, unless the user explicitly overrides a rule
3. QR Code Scanner app-specific documentation and implemented behavior
4. the shared ThinApps Android Guidelines
5. existing repository conventions not covered above

App-specific documentation is the final source of truth when QR Code Scanner intentionally differs from a shared guideline. Do not change working app behavior solely to force consistency with a general guideline when the difference is deliberate and documented.

## Product Guardrails

Preserve QR Code Scanner as a focused, local-only QR code and barcode scanner.

- Keep live camera scanning and Android Photo Picker image scanning aligned with the supported formats and behavior documented under `docs/`.
- Preserve the privacy-first baseline: no accounts, ads, analytics, tracking, Internet permission, cloud processing, or remote scan history unless the user explicitly approves and the related documentation and disclosures are updated.
- Keep scan history local, limited, and consistent with the documented deduplication, timestamp, preview, and clearing behavior.
- Preserve safe URL normalization and validation before enabling Open, and keep Copy, Share, and external-app handling accurately documented.
- Keep torch control inside the CameraX camera flow unless an approved change updates the implementation and documentation together.
- Prefer small, reliable changes over speculative features, broad refactors, or additional dependencies.

## Mandatory Repository Policy

- Work directly on the default `master` branch.
- Use exactly one commit per edited file.
- Keep all approved related edits to one file together in that file's single commit.
- Commit separate code, resource, documentation, version, configuration, and workflow files separately.
- Do not split one file into several unnecessary commits unless explicitly requested.
- Do not use temporary branches, pull requests, Git trees or blobs, helper workflows, generated patch workflows, squashing, amending, force-pushing, or history rewriting.
- Never use GitHub Actions to create, apply, or commit normal repository changes.
- Before every commit, inspect active workflow triggers and determine whether the commit would start GitHub Actions directly or indirectly.
- Do not create a commit that would trigger an Actions run unless the user explicitly approves the expected run.
- Routine source, documentation, changelog, versioning, and maintenance edits must not automatically consume Actions minutes.
- After each repository change, report the commit SHA, commit message, and exact file changed.

## GitHub Actions

The release workflow in `.github/workflows/android-release.yml` is manual-only through `workflow_dispatch`.

- Do not dispatch the workflow unless the user explicitly requests or approves the run.
- Do not add `push`, `pull_request`, `schedule`, `workflow_run`, `repository_dispatch`, or another automatic trigger without explicit approval and a documented reason.
- Do not change a manual-only workflow into an automatic workflow as part of unrelated work.
- For a release, complete all approved one-file commits first, confirm that version and documentation sources are aligned, and dispatch the intended workflow from the final commit.
- Inspect failures before rerunning a workflow; do not repeatedly rerun unexplained failures.

## Change Review

Before completing a task:

- review the full affected flow rather than only the edited lines
- confirm code, resources, manifest, Gradle configuration, workflows, and documentation remain consistent where relevant
- avoid unrelated cleanup or cosmetic churn
- update app-specific documentation or the changelog when user-facing or release behavior requires it, using separate one-file commits
- state any limitation, unverified assumption, or intentionally deferred follow-up clearly
