# Scanner Result Handling

QR Code Scanner keeps the camera preview and image analyzer active after a QR code is found. The app does not pause the scanner or switch to a separate result screen.

To avoid duplicate result spam, detected QR values pass through a small result gate before the UI is updated.

## Screen text states

The main scanner screen keeps the large title and a smaller subtitle/status line above the result card. A history icon sits beside the title and opens the local scan history screen.

Before a QR code is found:

- the subtitle/status line says `Point your camera at a QR code.`
- the result card says `No QR code scanned yet.`

After a QR code is found:

- the subtitle/status line says `Preview the scanned result.`
- the result card shows the scanned value
- a small close icon appears in the result card header so the visible result can be cleared

Permission and camera failure messages also use the subtitle/status line. See [Permissions](permissions.md) for those states.

## Result clearing

The clear icon only clears the currently visible in-memory result. It does not clear saved scan history. See [Scan History](history.md) for the saved local history behavior.

When the result is cleared:

- the visible result returns to `No QR code scanned yet.`
- the subtitle/status line returns to `Point your camera at a QR code.`
- Copy, Open, and Share become disabled again
- the same just-cleared value remains suppressed briefly if it is still in the camera frame

## Result gate behavior

- A value must be detected more than once before it is accepted.
- After a result is accepted, another result cannot replace it immediately.
- The same accepted value is ignored for a short duplicate window.
- If the same value remains continuously in view, the duplicate window keeps sliding forward so the UI does not keep refreshing the same result.
- A different QR code can still be accepted after the short cooldown.

This keeps scanning live while avoiding noisy repeated updates from the same QR code or quick flicker between detections.

Accepted results are saved into local scan history after passing this gate.

## Why there is no Scan Again flow yet

A stricter result flow would pause or lock scanning after the first accepted QR code, show the result, and require the user to tap a Scan Again button before another QR value could be accepted.

That flow can make sense later if the app should feel more like a deliberate capture tool. For now, the app stays closer to a lightweight live scanner: it keeps the camera active, filters duplicate noise, and still lets a different QR code replace the current result after a short cooldown.

This avoids adding another large button and another scanner state before testing proves the extra step is useful. The current result gate solves the immediate spam problem without making normal scanning slower or more complicated.

## Current constants

The current gate uses these values in `MainActivity.kt`:

- `REQUIRED_SCAN_HITS = 2`
- `RESULT_COOLDOWN_MS = 1000L`
- `SAME_RESULT_IGNORE_MS = 6000L`

These values are intentionally conservative. They should make scanning feel calmer without making normal single-code scanning feel slow.
