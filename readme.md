# QR Code Scanner

QR Code Scanner is an open-source Android utility for scanning QR codes and common barcodes through the live camera or Android Photo Picker. It supports local result actions, validated web-link opening, pinch-to-zoom, tap-to-focus, torch control, and a private on-device scan history.

The app's core functionality works locally without an internet connection, accounts, ads, ThinApps-operated analytics or tracking, or cloud processing. Scan history stays on the device and keeps only the 50 most recent unique values.

## Documentation

QR Code Scanner follows the shared ThinApps Guidelines by default; the app-specific documentation in this repository takes precedence where it records an intentional product or technical difference.

| Document | Description |
| --- | --- |
| [Agent Instructions](agents.md) | Defines persistent repository instructions, precedence, product guardrails, commit policy, workflow rules, and review expectations for coding agents. |
| [Scope](docs/scope.md) | Explains what barcode formats and scan sources are currently supported and what intentionally remains out of scope. |
| [Build](docs/build.md) | Explains Gradle, release workflow, signing, R8, ProGuard, and the temporary Gradle-wrapper strategy. |
| [Actions](docs/actions.md) | Explains copy, share, open, URL normalization, and history-preview behavior for scanned results. |
| [Scanning](docs/scanning.md) | Explains live camera scanning, selected-image scanning, preview focus and zoom behavior, and duplicate-result filtering. |
| [History](docs/history.md) | Explains local-only scan history, saved fields, limits, preview behavior, spacing, and clearing behavior. |
| [Interface](docs/interface.md) | Explains scanner text sizes, corner radiuses, launcher icon, icon buttons, preview gestures, spacing, and visible screen states. |
| [Accessibility](docs/accessibility.md) | Explains the accessibility baseline, intentionally deferred behavior, testing policy, and possible future improvements. |
| [Permissions](docs/permissions.md) | Explains Camera permission, Android Photo Picker access, and how permission or camera errors appear in the app. |
| [Torch](docs/torch.md) | Explains why scanner torch control stays inside CameraX instead of using standalone CameraManager torch control. |
| [Changelog](changelog.md) | Lists the complete version history and released changes. |
| [ThinApps Guidelines](https://github.com/thinapps/android-guidelines) | Provides the shared product, Android, repository, workflow, privacy, Google Play, and release defaults used across ThinApps projects. |
