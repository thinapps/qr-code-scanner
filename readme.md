# QR Code Scanner

Scans QR codes and barcodes with history

## Documentation

| Document | Description |
| --- | --- |
| [Scope](docs/scope.md) | Explains what barcode formats and scan sources are currently supported and what intentionally remains out of scope. |
| [Build](docs/build.md) | Explains Gradle, release workflow, signing, R8, and ProGuard choices. |
| [Actions](docs/actions.md) | Explains copy, share, open, URL normalization, and history-preview behavior for scanned results. |
| [Scanning](docs/scanning.md) | Explains live camera scanning, selected-image scanning, preview focus/zoom behavior, and duplicate result filtering. |
| [History](docs/history.md) | Explains local-only scan history, saved fields, limits, preview behavior, spacing, and clearing behavior. |
| [Interface](docs/interface.md) | Explains scanner text sizes, corner radiuses, launcher icon, icon buttons, preview gestures, spacing, and visible screen text states. |
| [Accessibility](docs/accessibility.md) | Explains the current accessibility baseline, intentionally deferred behavior, testing policy, and possible future improvements. |
| [Permissions](docs/permissions.md) | Explains the camera permission, Android Photo Picker access, and how permission or camera errors appear in the app. |
| [Torch](docs/torch.md) | Explains why scanner torch control stays inside CameraX instead of using standalone CameraManager torch control. |

## Changelog

### 0.9.0
- adds scanning QR codes and barcodes from a selected image

### 0.8.4
- restores the currently visible scanned result after Android recreates the scanner screen
- aligns tap-to-focus with CameraX's standard focus and metering action at the tapped preview point

### 0.8.3
- keeps `Allow Camera` as the single permission button label while opening app settings when Android no longer shows the camera permission dialog

### 0.8.2
- rejects backslashes, control characters, and Unicode bidirectional-control characters before enabling Open for scanned web links

### 0.8.1
- adds tap-to-focus on the camera preview while keeping pinch zoom separate from tap gestures
- simplifies scan history metadata to timestamp-only so history does not add result type labels

### 0.8.0
- adds manual pinch-to-zoom on the camera preview without adding zoom buttons, a slider, saved zoom state, or auto-zoom behavior

### 0.7.8
- bumps Android Gradle Plugin from `8.5.2` to `8.7.3` to stay aligned with Material Components `1.13.0`
- switches `MainActivity` and `HistoryActivity` from `ComponentActivity` to `AppCompatActivity` for better Material/AppCompat XML compatibility
- renames torch accessibility string resources from flashlight wording to torch wording

### 0.7.7
- returns the torch button to standard click-only activation and removes the custom touch-down listener
- moves the bottom scanner panel top-edge height into shared `dimens.xml`
- removes the unused `md_primary` color resource

### 0.7.6
- thins the scan guide stroke from `6` to `5`
- softens the scan guide opacity from `#8CDADADA` at about 55% opacity to `#7FDADADA` at about 50% opacity
- polishes scanner subtitle/status messages for clearer and more balanced state text
- removes the custom share chooser title so Android can show its native share sheet without app-specific title text

### 0.7.5
- increases the scan guide target from 72% to 80% of the preview width
- removes the fixed `320dp` maximum so available camera width and height control the final size
- removes torch-clearance shrinking while keeping dynamic centering above the bottom scanner panel

### 0.7.4
- sizes the scan guide from 72% of the visible preview width instead of a fixed preferred size
- caps the guide between `240dp` and `320dp` while keeping cramped-layout shrink behavior

### 0.7.3
- increases the scan guide preferred maximum from `260dp` to `280dp`
- softens the scan guide gray from `#99DADADA` to `#8CDADADA`

### 0.7.2
- increases the scan guide from `240dp` to a preferred maximum of `260dp`
- lets the guide shrink on cramped preview areas so it does not crowd the torch button, screen edges, or bottom scanner panel
- avoids redundant scan guide layout updates after the adaptive size is already applied

### 0.7.1
- fixes scan guide alignment by drawing the guide as a separate overlay instead of a `PreviewView` foreground
- centers the guide in the visible camera area above the bottom scanner panel
- changes the scan guide from accent cyan to neutral semi-transparent gray (`#99DADADA`)
- removes the unused scan guide foreground wrapper resource

### 0.7.0
- adds scanning support for the common ML Kit barcode formats: QR Code, EAN-13, EAN-8, UPC-A, UPC-E, Code 128, Code 39, Code 93, Codabar, ITF, PDF417, Aztec, and Data Matrix
- does not support Micro QR, rectangular Micro QR, UPC/EAN 2-digit or 5-digit add-on codes, GS1 DataBar/RSS, GS1 Composite, MicroPDF417, MaxiCode, DotCode, postal barcodes, MSI, Plessey, Code 11, Pharmacode, or proprietary color, circular, or app-specific codes
- updates the launcher label, scanner title, app copy, documentation, and scope notes for QR code and barcode scanning

### 0.6.3
- adds a subtle centered scan guide overlay over the scanner screen
- keeps the guide visual-only, non-clickable, and separate from QR detection behavior

### 0.6.2
- shrinks the transparent adaptive icon foreground so the QR glyph has more breathing room under circular and rounded launcher masks
- documents the safer launcher-icon sizing target after adaptive icon mask testing

### 0.6.1
- switches the installed launcher icon from direct `drawable` PNG wiring to `mipmap` adaptive icon wiring
- adds foreground/background adaptive icon layers for Android 8.0 and newer while keeping one shared foreground PNG source
- replaces the foreground asset with a transparent cyan QR glyph sized and padded for the adaptive icon safe area
- adds `mipmap-anydpi` fallback wrappers for older launchers and points both normal and round icon fields at the mipmap resources

### 0.6.0
- strengthens scanned result monospace rendering by applying Android's `Typeface.MONOSPACE` object through the text span on Android 9 and newer
- clarifies the found-result status text and shortens the permission button label
- replaces the vector launcher icon with a padded 512px PNG launcher icon

### 0.5.5
- forces scanned result values through a monospace typeface and monospace text span so Android devices are less likely to render them with proportional-looking fallback text
- aligns the scan history list-to-clear-button gap with the existing 20dp section spacing

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
- changes the inactive torch button background from 30% black (`#4D000000`) to 40% black (`#66000000`) for better visibility over bright camera previews
- keeps the active torch button background fully opaque cyan (`#00BCD4`)
- adds a 10% white (`#1AFFFFFF`) tap ripple to the history icon

### 0.5.1
- adds a 48dp back button to the scan history screen
- increases the history and result-close icon buttons to 48dp touch targets while keeping 24dp icons
- aligns result, action, button, and history spacing with the 8dp layout rhythm
- clarifies the shared weighted layout dimension name used by scanner and history views

### 0.5.0
- adds local-only scan history for accepted QR results
- keeps the most recent 50 unique values and moves repeat scans back to the top
- adds a title-row history icon and a history screen with tap-to-preview behavior
- adds a Clear All History action while keeping the result-card close icon limited to hiding the current result

### 0.4.3
- changes the scanner result card radius from 12dp to 16dp
- adds a close icon for clearing the visible scanned result
- centralizes scanner layout measurements in `dimens.xml`

### 0.4.2
- changes the scanner subtitle/status text size from 15sp to 14sp
- changes the scanner result card radius from 8dp to 12dp
- uses an oval image button for the scanner torch control

### 0.4.1
- keeps the scanner subtitle visible for normal, permission, and camera states
- tightens the empty result card so it no longer repeats the scan prompt
- shortens the found-result status message above the result card
- changes the scanner result card radius from 24dp to 8dp

### 0.4.0
- syncs the torch button with the real CameraX torch state
- handles failed torch requests by restoring the button to the current torch state
- makes the active torch button easier to see with an accent background and filled icon

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
- documented why scanner torch control stays inside CameraX

### 0.2.2
- made the torch toggle respond on touch-down instead of waiting for click release
- added haptic feedback for torch toggles
- added an explicit ripple color for the torch icon button
- matched the torch overlay alpha to the Screen Light back button pattern
- tightened scanner horizontal gutters to 20dp
- made the bottom scanner panel flush with the screen edges while keeping 20dp inner padding

### 0.2.1
- moved the torch toggle to a top-right scanner overlay
- replaced the wide torch button with a white flash icon on a circular dark background
- kept the torch control hidden unless the active camera supports flash

### 0.2.0
- added a scanner torch toggle
- shows the torch control only when the active camera supports flash
- tightened basic XML styling for the scanner screen and buttons
- polished torch button states so off is outlined and on filled accent

### 0.1.0
- initialized the Android project framework
- added a basic single-screen QR scanning flow
- added local-only result preview actions
- added release signing config and manual GitHub Actions AAB workflow
- documented the intentionally small build setup
