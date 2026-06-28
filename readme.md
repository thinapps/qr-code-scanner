# QR Code Scanner

Lightweight Android QR code scanner built with the same basic classic Android/XML approach used by other ThinApps utilities.

## Documentation

| Document | Description |
| --- | --- |
| [Build](docs/build.md) | Explains Gradle, release workflow, signing, R8, and ProGuard choices. |

## Status

This repo currently contains the starter app framework only:

- single Kotlin activity
- XML layout with view binding
- Camera permission flow
- CameraX preview and frame analysis
- ML Kit QR code detection
- scanner flashlight toggle when the active camera supports flash
- local result preview with copy, open, and share actions
- manual GitHub Actions release workflow for signed AAB builds

No history, ads, accounts, analytics, networking, or database layer has been added.

## Changelog

### 0.1.1
- added a scanner flashlight toggle
- shows the flashlight control only when the active camera supports flash

### 0.1.0
- initialized the Android project framework
- added a basic single-screen QR scanning flow
- added local-only result preview actions
- added release signing config and manual GitHub Actions AAB workflow
- documented the intentionally small build setup
