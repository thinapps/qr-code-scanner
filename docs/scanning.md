# Scanner Result Handling

QR Code Scanner keeps the camera preview and image analyzer active after a QR code is found. The app does not pause the scanner or switch to a separate result screen.

To avoid duplicate result spam, detected QR values pass through a small result gate before the UI is updated.

## Result gate behavior

- A value must be detected more than once before it is accepted.
- After a result is accepted, another result cannot replace it immediately.
- The same accepted value is ignored for a short duplicate window.
- If the same value remains continuously in view, the duplicate window keeps sliding forward so the UI does not keep refreshing the same result.
- A different QR code can still be accepted after the short cooldown.

This keeps scanning live while avoiding noisy repeated updates from the same QR code or quick flicker between detections.

## Current constants

The current gate uses these values in `MainActivity.kt`:

- `REQUIRED_SCAN_HITS = 2`
- `RESULT_COOLDOWN_MS = 1000L`
- `SAME_RESULT_IGNORE_MS = 6000L`

These values are intentionally conservative. They should make scanning feel calmer without making normal single-code scanning feel slow.
