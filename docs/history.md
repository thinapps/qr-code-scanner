# Scan History

QR Code Scanner keeps a small local history of accepted camera and selected-image results.

The app intentionally has a history screen, but the history screen should stay lightweight:

- stored locally on the device with private app preferences
- no account
- no sync
- no internet access
- no analytics
- no database
- no search
- no export
- no filters
- no labels
- no folders
- no favorites

The goal is a simple recent-results list inside the scanner, not a broader organizing, archiving, reporting, or database-style feature.

## What gets saved

A live camera result is saved only after the camera result gate accepts it. This avoids saving every camera-frame detection or duplicate scanner noise.

A selected-image result is saved immediately after the first supported value is found because the app analyzes only one still image. It uses the same repository and duplicate rules as a camera result.

Each history item stores:

- scanned value
- scan timestamp
- whether the value was openable as a web link when it was scanned

The openable-web-link flag is internal history metadata. It is not shown as a result type label on the history row.

History does not store whether a result came from the camera or an image. It also does not store or copy the selected image, its URI, its filename, or a thumbnail.

The app keeps the most recent 50 unique values. If the same value is scanned again from either source, it moves back to the top with a new timestamp instead of creating a duplicate row.

## History screen

The history icon in the scanner title row opens the history screen. The icon remains visible even when history is empty so the feature is discoverable.

The history screen shows saved results in newest-first order. Each row shows the scanned value and a small timestamp metadata line.

The history screen uses the same 20dp side gutters as the scanner panel. The subtitle sits 8dp below the title row, saved history rows start 20dp below the subtitle, rows are separated by 8dp, and the Clear All History button sits 20dp below the scrollable history list area. The screen keeps 20dp base bottom padding, plus the bottom system-bar inset when Android reports one.

Tapping a saved result returns it to the main scanner screen for preview, copy, open, or share actions. Previewing a saved history item does not create a new history entry or move that item to the top of the history list.

Restoring the currently visible result after Android recreates the scanner screen also does not create a new history entry or change the saved history order.

## What history does not do

The history screen does not include search, export, batch lists, inventory-style workflows, source labels, result type labels, filters, folders, favorites, pinning, tagging, editing, notes, cloud sync, image thumbnails, or saved collections.

Those features are intentionally skipped because this app is a simple local scanner with a small recent-results history screen. Expanding history into searchable, filterable, editable, exportable, image-aware, or synced workflows would add storage, privacy, duplicate-handling, UI, and maintenance decisions that do not fit the lightweight design.

Future history changes should be limited to small reliability or clarity fixes. They should not turn the history screen into a complicated organizing system.

## Clearing history

The history screen includes a Clear All History button. Clearing history removes the saved local history list only. The button is disabled while history is empty.

The close icon in the scanner result card is separate. It only hides the currently visible result from the main scanner screen and does not clear saved history.
