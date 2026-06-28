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
- top-right scanner flashlight toggle when the active camera supports flash
- local result preview with copy, open, and share actions
- manual GitHub Actions release workflow for signed AAB builds

No history, ads, accounts, analytics, networking, or database layer has been added.

## Changelog

### 0.2.1
- moved the flashlight toggle to a top-right scanner overlay
- replaced the wide flashlight button with a white flash icon on a circular dark background
- kept the flashlight control hidden unless the active camera supports flash

### 0.2.0
- added a scanner flashlight toggle
- shows the flashlight control only when the active camera supports flash
- tightened basic XML styling for the scanner screen and buttons
- polished flashlight button states so off is outlined and on is filled accent

### 0.1.0
- initialized the Android project framework
- added a basic single-screen QR scanning flow
- added local-only result preview actions
- added release signing config and manual GitHub Actions AAB workflow
- documented the intentionally small build setup
