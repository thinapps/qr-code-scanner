# QR Code Scanner

Lightweight Android QR code scanner built with the same basic classic Android/XML approach used by other ThinApps utilities.

## Documentation

| Document | Description |
| --- | --- |
| [Build](docs/build.md) | Explains Gradle, release workflow, signing, R8, and ProGuard choices. |
| [Actions](docs/actions.md) | Explains copy, share, open, and URL normalization behavior for scanned results. |
| [Scanning](docs/scanning.md) | Explains how live scanning stays active while duplicate result spam is filtered. |
| [Torch](docs/torch.md) | Explains why scanner flashlight control stays inside CameraX instead of using standalone CameraManager torch control. |

## Changelog

### 0.4.1
- keeps the scanner subtitle visible for normal, permission, and camera states
- tightens the empty result card so it no longer repeats the scan prompt
- shortens the found-result status message above the result card

### 0.4.0
- syncs the flashlight button with the real CameraX torch state
- handles failed torch requests by restoring the button to the current torch state
- makes the active flashlight button easier to see with an accent background and filled icon

### 0.3.2
- enabled opening web domains without an explicit http or https scheme
- normalizes schemeless web domains to https links before opening
- rejects web links with URI user-info before enabling Open

### 0.3.1
- centered the local-only footer message below the scanner result card
- added haptic feedback when a QR code is captured
- added haptic feedback to result action buttons
- lightened the scanner result card and stroke for better result contrast

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
