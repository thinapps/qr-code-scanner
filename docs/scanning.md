# Scanner Result Handling

QR Code Scanner keeps the camera preview and image analyzer active after a QR code or barcode is found. The app does not pause the scanner or switch to a separate result screen.

See [Scope](scope.md) for the current supported and unsupported barcode format lists.

To avoid duplicate result noise, detected values pass through a small result gate before the UI is updated.

The centered scan guide is visual only. It gives users an aiming reference, but it does not crop the camera frame, limit detection to the guide area, or change the result gate behavior.

The guide is dynamic and follows the visible camera portion of the screen. It targets 80% of the preview width, keeps a `240dp` minimum, and no longer uses a fixed maximum cap. The app uses the top of the bottom scanner panel as the lower edge of the visible camera area, so if the bottom panel changes height because of result, permission, footer, or system-inset changes, the guide is recalculated and re-centered above it.

The guide still shrinks if the available camera width or height is genuinely cramped, but the torch button no longer reduces the guide size just because it exists in the top-right corner. Root layout, bottom panel, guide overlay, torch button, and window inset changes still trigger recalculation so the guide stays aligned across different screen sizes, system bars, and cutout configurations.

If the guide and torch ever visually overlap on a very cramped device, the torch remains above the guide in the root `FrameLayout` draw order. The guide is also non-clickable, non-focusable, and hidden from accessibility, so it should not block torch taps or steal accessibility focus. This keeps the edge case safe without shrinking the guide for every normal phone.

## Screen text states

The main scanner screen keeps the large title and a smaller subtitle/status line above the result card. A history icon sits beside the title and opens the local scan history screen.

Before a scan result is found:

- the subtitle/status line says `Point your camera at a QR code or barcode.`
- the result card says `No QR code or barcode scanned yet.`

After a scan result is found:

- the subtitle/status line says `Preview the scanned result.`
- the result card shows the scanned value
- a small close icon appears in the result card header so the visible result can be cleared

Permission and camera failure messages also use the subtitle/status line. See [Permissions](permissions.md) for those states.

## Result clearing

The clear icon only clears the currently visible in-memory result. It does not clear saved scan history. See [Scan History](history.md) for the saved local history behavior.

When the result is cleared:

- the visible result returns to `No QR code or barcode scanned yet.`
- the subtitle/status line returns to `Point your camera at a QR code or barcode.`
- Copy, Open, and Share become disabled again
- the same just-cleared value remains suppressed briefly if it is still in the camera frame

## Result gate behavior

- A value must be detected more than once before it is accepted.
- After a result is accepted, another result cannot replace it immediately.
- The same accepted value is ignored for a short duplicate window.
- If the same value remains continuously in view, the duplicate window keeps sliding forward so the UI does not keep refreshing the same result.
- A different QR code or barcode can still be accepted after the short cooldown.

This keeps scanning live while avoiding repeated updates from the same QR code, barcode, or quick flicker between detections.

Accepted results are saved into local scan history after passing this gate.

## Why there is no Scan Again flow yet

A stricter result flow would pause or lock scanning after the first accepted QR code or barcode, show the result, and require the user to tap a Scan Again button before another value could be accepted.

That flow can make sense later if the app should feel more like a deliberate capture tool. For now, the app stays closer to a lightweight live scanner: it keeps the camera active, filters duplicate noise, and still lets a different QR code or barcode replace the current result after a short cooldown.

This avoids adding another large button and another scanner state before testing proves the extra step is useful. The current result gate solves the immediate repeat-update problem without making normal scanning slower or more complicated.

## Current constants

The current gate uses these values in `MainActivity.kt`:

- `REQUIRED_SCAN_HITS = 2`
- `RESULT_COOLDOWN_MS = 1000L`
- `SAME_RESULT_IGNORE_MS = 6000L`

These values are intentionally conservative. They should make scanning feel calmer without making normal single-code scanning feel slow.
