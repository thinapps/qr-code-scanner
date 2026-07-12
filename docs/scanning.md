# Scanner Result Handling

QR Code Scanner keeps the camera preview and image analyzer active after a QR code or barcode is found. The app does not pause the scanner or switch to a separate result screen.

See [Scope](scope.md) for the current supported and unsupported barcode format lists.

To avoid duplicate result noise, detected values pass through a small result gate before the UI is updated.

The centered scan guide is visual only. It gives users an aiming reference, but it does not crop the camera frame, limit detection to the guide area, or change the result gate behavior.

The guide is dynamic and follows the visible camera portion of the screen. It targets 80% of the preview width, keeps a `240dp` minimum, and no longer uses a fixed maximum cap. The app uses the top of the bottom scanner panel as the lower edge of the visible camera area, so if the bottom panel changes height because of result, permission, footer, or system-inset changes, the guide is recalculated and re-centered above it.

The guide uses a `5` vector-unit stroke on a `240x240` viewport and neutral semi-transparent gray (`#7FDADADA`, about 50% opacity). This replaced the previous `#8CDADADA`, which was about 55% opacity. This keeps the guide visible after dynamic scaling while avoiding a thick or overly opaque scanner-box feel.

The guide still shrinks if the available camera width or height is genuinely cramped, but the torch button no longer reduces the guide size just because it exists in the top-right corner. Root layout, bottom panel, guide overlay, torch button, and window inset changes still trigger recalculation so the guide stays aligned across different screen sizes, system bars, and cutout configurations.

If the guide and torch ever visually overlap on a very cramped device, the torch remains above the guide in the root `FrameLayout` draw order. The guide is also non-clickable, non-focusable, and hidden from accessibility, so it should not block torch taps or steal accessibility focus. This keeps the edge case safe without shrinking the guide for every normal phone.

## Preview focus and zoom

The camera preview supports manual pinch-to-zoom. The gesture applies CameraX zoom ratio changes to the active camera and clamps each update to the camera's reported minimum and maximum zoom ratios.

The app also supports tap-to-focus on the live preview. A normal single-finger tap creates a CameraX autofocus request at that preview point. The request uses autofocus-only metering so it explicitly triggers a focus scan without also locking exposure. It auto-cancels after a short timeout, checks CameraX's returned focus result, and logs unsupported or unsuccessful requests without interrupting scanning.

Tap-to-focus is kept separate from pinch zoom. Multi-touch gestures, active scale gestures, and moved touches are treated as zoom/gesture input instead of focus taps.

The app does not add visible plus/minus buttons, a zoom slider, a saved zoom preference, a focus ring UI, or ML Kit auto-zoom. Pinch zoom and tap-to-focus keep the scanner screen clean while still helping with small, distant, or slightly blurry QR codes and barcodes.

The app will not add ML Kit auto-zoom. Manual pinch zoom is enough for this scanner now, and auto-zoom can feel jumpy or unpredictable when the camera changes zoom on its own while the user is aiming.

## Screen text states

The main scanner screen keeps the large title and a smaller subtitle/status line above the result card. A history icon sits beside the title and opens the local scan history screen.

Before a scan result is found:

- the subtitle/status line says `Point your camera at a QR code or barcode.`
- the result card says `No QR code or barcode scanned yet.`

After a scan result is found:

- the subtitle/status line says `Preview the scanned result below.`
- the result card shows the scanned value
- a small close icon appears in the result card header so the visible result can be cleared

When camera permission is missing or denied:

- the subtitle/status line says `Camera permission is needed before scanning.`

When the camera cannot start:

- the subtitle/status line says `Error: camera could not start on this device.`

## Result clearing

The clear icon only clears the currently visible in-memory result. It does not clear saved scan history. See [Scan History](history.md) for the saved local history behavior.

When the result is cleared:

- the visible result returns to `No QR code or barcode scanned yet.`
- the subtitle/status line returns to `Point your camera at a QR code or barcode.`
- Copy, Open, and Share become disabled again
- the same just-cleared value remains suppressed briefly if it is still in the camera frame

## Result restoration after screen recreation

The app saves only the currently visible scanned value as small transient activity state. If Android recreates `MainActivity` after a rotation, another configuration change, or system-initiated process recreation, the result card, result status, clear icon, and result action states are restored.

Restoring a visible result does not add another history entry or replay the scan haptic. The restored value is also returned to the short duplicate-suppression window so the same code does not immediately fire again if it remains in the camera frame.

This is not permanent result storage. A new scanner screen after the user fully dismisses the previous activity starts empty, while saved scan history remains separate.

## Result gate behavior

- A value must be detected more than once before it is accepted.
- After a result is accepted, another result cannot replace it immediately.
- The same accepted value is ignored for a short duplicate window.
- If the same value remains continuously in view, the duplicate window keeps sliding forward so the UI does not keep refreshing the same result.
- A different QR code or barcode can still be accepted after the short cooldown.

This keeps scanning live while avoiding repeated updates from the same QR code, barcode, or quick flicker between detections.

Accepted results are saved into local scan history after passing this gate.

## Why there is no Scan Again flow

The app will not add a scan lock, capture lock, or Scan Again flow. That stricter pattern would pause or lock scanning after the first accepted QR code or barcode, show the result, and require another tap before scanning could continue.

That behavior sounds controlled, but it would likely make a lightweight scanner feel slower and more annoying. The app intentionally keeps live scanning active, uses the result gate to reduce duplicate noise, and still lets users clear the visible result without adding another scanner state or large button.

## Current constants

The current gate uses these values in `MainActivity.kt`:

- `REQUIRED_SCAN_HITS = 2`
- `RESULT_COOLDOWN_MS = 1000L`
- `SAME_RESULT_IGNORE_MS = 6000L`

These values are intentionally conservative. They should make scanning feel calmer without making normal single-code scanning feel slow.
