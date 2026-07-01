# QR Code Scanner

Lightweight Android QR code scanner built with the same basic classic Android/XML approach used by other ThinApps utilities.

## Documentation

| Document | Description |
| --- | --- |
| [Build](docs/build.md) | Explains Gradle, release workflow, signing, R8, and ProGuard choices. |
| [Actions](docs/actions.md) | Explains copy, share, open, URL normalization, and history-preview behavior for scanned results. |
| [Scanning](docs/scanning.md) | Explains how live scanning stays active while duplicate result spam is filtered. |
| [History](docs/history.md) | Explains local-only scan history, saved fields, limits, preview behavior, and clearing behavior. |
| [Interface](docs/interface.md) | Explains scanner text sizes, corner radiuses, icon buttons, and visible screen text states. |
| [Permissions](docs/permissions.md) | Explains the camera permission, why it is needed, and how permission or camera errors appear in the app. |
| [Torch](docs/torch.md) | Explains why scanner flashlight control stays inside CameraX instead of using standalone CameraManager torch control. |

## Changelog

### 0.5.5
- forces scanned result values through a monospace typeface and monospace text span so Android devices are less likely to render them with proportional-looking fallback text

### 0.5.4
- reserves the result-card close icon slot so the result header does not shift when a QR result appears or is cleared
- adds the shared 5% white (`#0DFFFFFF`) tap ripple to the history-screen back button
- removes the app-level Copy toast so Android's native clipboard confirmation is the only copy feedback
- adds a subtle 1dp top edge to the bottom scanner panel for better separation over dark camera previews
- enforces monospace rendering for scanned result values in code and XML

### 0.5.3
- softens the history icon tap ripple from 10% white (`#1AFFFFFF`) to 5% white (`#0DFFFFFF`)
- adds the shared 5% white (`#0DFFFFFF`) tap ripple to the result-card close icon
- replaces the history-specific ripple drawable with a shared icon button ripple drawable
- tightens the result card top padding from `20dp` to `12dp` while keeping side and bottom padding at `20dp`
- changes scanned result values to 16sp monospace text

### 0.5.2
- changes the inactive flashlight button background from 30% black (`#4D000000`) to 40% black (`#66000000`) for better visibility over bright camera previews
- keeps the active flashlight button background fully opaque cyan (`#00BCD4`)
- adds a 10% white (`#1AFFFFFF`) tap ripple to the history icon

### 0.5.1
- adds a 48dp back button to the scan history screen
- increases the history and result-close icon buttons to 48dp touch targets while keeping 24dp icons
- aligns result, action, button, and history spacing with the 8dp layout rhythm
- clarifies the shared weighted layout dimension name used by scanner and history views

### 0.5.0
- adds local-only scan history for accepted QR results
- keeps the most recent 50 unique scan values and moves repeat scans back to the top
- adds a title-row history icon and a history screen with tap-to-preview behavior
- adds a Clear All History action while keeping the result-card close icon limited to hiding the current result

### 0.4.3
- changes the scanner result card radius from 12dp to 16dp
- adds a close icon for clearing the visible scanned result
- centralizes scanner layout measurements in `dimens.xml`

### 0.4.2
- changes the scanner subtitle/status text size from 15sp to 14sp
- changes the scanner result card radius from 8dp to 12dp
- uses an oval image button for the scanner flashlight control

### 0.4.1
- keeps the scanner subtitle visible for normal, permission, and camera states
- tightens the empty result card so it no longer repeats the scan prompt
- shortens the found-result status message above the result card
- changes the scanner result card radius from 24dp to 8dp

### 0.4.0
- syncs the flashlight button with the real CameraX torch state
- handles failed torch requests by restoring the button to the current torch state
- makes the active flashlight button easier to see with an accent background and filled icon

### 0.3.2
- supports domain-style scanned values without a typed scheme
- normalizes domain-style scanned values before opening
- blocks unsafe scanned links before enabling Open

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
