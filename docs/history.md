# Scan History

QR Code Scanner keeps a small local history of accepted scan results.

The history is intentionally lightweight:

- stored locally on the device with private app preferences
- no account
- no sync
- no internet access
- no analytics
- no database

## What gets saved

A scan is saved only after the scanner result gate accepts it. This avoids saving every camera-frame detection or duplicate scanner noise.

Each history item stores:

- scanned value
- scan timestamp
- whether the value was openable as a web link when it was scanned

The app keeps the most recent 50 unique values. If the same value is scanned again, it moves back to the top with a new timestamp instead of creating a duplicate row.

## History screen

The history icon in the scanner title row opens the history screen. The icon remains visible even when history is empty so the feature is discoverable.

The history screen shows saved results in newest-first order. Each row shows the scanned value and a small metadata line for the result type and timestamp.

Tapping a saved result returns it to the main scanner screen for preview, copy, open, or share actions. Previewing a saved history item does not create a new history entry or move that item to the top of the history list.

## Clearing history

The history screen includes a Clear All History button. Clearing history removes the saved local history list only. The button is disabled while history is empty.

The close icon in the scanner result card is separate. It only hides the currently visible result from the main scanner screen and does not clear saved history.
