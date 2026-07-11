# Scan History

QR Code Scanner keeps a small local history of accepted scan results.

The history is intentionally lightweight:

- stored locally on the device with private app preferences
- no account
- no sync
- no internet access
- no analytics
- no database
- no search
- no export

## What gets saved

A scan is saved only after the scanner result gate accepts it. This avoids saving every camera-frame detection or duplicate scanner noise.

Each history item stores:

- scanned value
- scan timestamp
- whether the value was openable as a web link when it was scanned

The openable-web-link flag is internal history metadata. It is not shown as a result type label on the history row.

The app keeps the most recent 50 unique values. If the same value is scanned again, it moves back to the top with a new timestamp instead of creating a duplicate row.

## History screen

The history icon in the scanner title row opens the history screen. The icon remains visible even when history is empty so the feature is discoverable.

The history screen shows saved results in newest-first order. Each row shows the scanned value and a small timestamp metadata line.

The history screen uses the same 20dp side gutters as the scanner panel. The subtitle sits 8dp below the title row, saved history rows start 20dp below the subtitle, rows are separated by 8dp, and the Clear All History button sits 20dp below the scrollable history list area. The screen keeps 20dp base bottom padding, plus the bottom system-bar inset when Android reports one.

Tapping a saved result returns it to the main scanner screen for preview, copy, open, or share actions. Previewing a saved history item does not create a new history entry or move that item to the top of the history list.

## What history does not do

The history screen does not include search, export, batch lists, inventory-style workflows, or result type labels.

Those features are intentionally skipped because this app is a simple local scanner. Turning history into a searchable or exportable archive would be too much bloat for the current app and would add storage, privacy, duplicate-handling, and UI decisions that do not fit the lightweight design.

## Clearing history

The history screen includes a Clear All History button. Clearing history removes the saved local history list only. The button is disabled while history is empty.

The close icon in the scanner result card is separate. It only hides the currently visible result from the main scanner screen and does not clear saved scan history.
