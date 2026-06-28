# QR Code Scanner

Lightweight Android QR code scanner built with the same basic classic Android/XML approach used by other ThinApps utilities.

## Documentation

| Document | Description |
| --- | --- |
| [Build](docs/build.md) | Explains Gradle, release workflow, signing, R8, and ProGuard choices. |
| [Scanning](docs/scanning.md) | Explains how live scanning stays active while duplicate result spam is filtered. |
| [Torch](docs/torch.md) | Explains why scanner flashlight control stays inside CameraX instead of using standalone CameraManager torch control. |

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

### 0.3.0
- added a scanner result gate that requires repeated detections before accepting a QR value
- added a short cooldown before one accepted result can replace another
- suppresses repeated updates for the same QR code while it remains in view
- documented scanner result handling

### 0.2.3
- removed the status bar top inset from the bottom scanner panel
- kept the bottom navigation inset so the panel stays clear of system controls
- added a local-only open-source footer message below the scanner result card
- documented why scanner flashlight control stays inside CameraX

### 0.2.2
- made the flashlight toggle respond on touch-down instead of waiting for click release
- added haptic feedback for flashlight toggles
- added an explicit ripple color for the flashlight icon button
- matched the flashlight overlay alpha to the Screen Light back button pattern
- tightened scanner horizontal gutters to 20dp
- made the bottom scanner panel flush with the screen edges while keeping 20dp inner padding

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
