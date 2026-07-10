# Scope

QR Code Scanner is a small local scanner for QR codes and common barcode formats supported by ML Kit.

## Supported now

The scanner currently enables every barcode format exposed by the app's ML Kit barcode scanner dependency:

- QR Code
- EAN-13
- EAN-8
- UPC-A
- UPC-E
- Code 128
- Code 39
- Code 93
- Codabar
- ITF
- PDF417
- Aztec
- Data Matrix

These cover the common consumer, retail, logistics, ticketing, ID, and compact 2D barcode families that ML Kit supports directly.

## Supported with practical caveats

Some supported formats are more demanding than others:

- EAN and UPC are the main retail product formats and should be part of any normal barcode scanner.
- Code 128, Code 39, Code 93, Codabar, and ITF cover common linear formats used across packaging, inventory, labels, libraries, and older systems.
- Aztec is useful for some ticketing and transport codes, so it is enabled even though QR is more familiar to most users.
- PDF417 can be large and dense. It may need a clearer, closer, or higher-resolution camera view than simple retail barcodes.
- Data Matrix is useful for compact 2D labels, but ML Kit only recognizes Data Matrix codes that intersect the center point of the input image.

## Not supported by the current app

The app does not currently support barcode families outside the ML Kit barcode formats enabled above. These are unsupported now:

| Format family | Why it is not supported now |
| --- | --- |
| Micro QR / rectangular Micro QR | Not exposed by the current ML Kit barcode scanner dependency. Supporting it would require another decoder or custom scanning path. |
| UPC/EAN 2-digit and 5-digit add-on codes | Not exposed by the current ML Kit barcode scanner dependency. These add-ons can appear beside EAN or UPC codes on magazines, periodicals, and books, but the app currently targets the main EAN or UPC value. |
| GS1 DataBar / RSS | Not exposed by the current ML Kit barcode scanner dependency. It can appear in retail, coupon, produce, and healthcare contexts, but it would require another decoder. |
| GS1 Composite | Not exposed by the current ML Kit barcode scanner dependency. Composite codes combine a linear barcode with an extra stacked component, so they need broader symbology support than this app currently has. |
| MicroPDF417 | Not exposed by the current ML Kit barcode scanner dependency. It is a specialized stacked barcode family separate from PDF417. |
| MaxiCode | Not exposed by the current ML Kit barcode scanner dependency. It is mostly a specialized logistics format, so it is outside the current consumer scanner target. |
| DotCode | Not exposed by the current ML Kit barcode scanner dependency. It is a specialized marking format rather than a normal QR or retail barcode target. |
| Postal barcodes | Not exposed by the current ML Kit barcode scanner dependency. Postal formats are country- and routing-specific and are outside the current app scope. |
| MSI, Plessey, Code 11, Pharmacode, and similar legacy linear codes | Not exposed by the current ML Kit barcode scanner dependency. They are niche or legacy formats compared with EAN, UPC, Code 128, Code 39, Code 93, Codabar, and ITF. |
| Proprietary color, circular, or app-specific codes | These are not standard ML Kit barcode formats. They would need a vendor SDK, custom decoder, or a specific use case before being added. |

The short version: the app supports every barcode format ML Kit exposes here, including dense or stricter formats like PDF417 and Data Matrix. It does not support barcode families that ML Kit does not expose without adding another scanner library or custom decoding work.

## Result handling

All accepted scan results use the same simple result flow:

- preview the scanned value
- copy it
- share it
- open it only when it looks like a safe web link
- save it in local scan history

Scanning happens locally on the device. The app requests only camera permission.

## Intentionally out of scope

The app is not trying to be a warehouse, inventory, enterprise scanning tool, shopping lookup app, QR creation toolkit, or multi-purpose QR action launcher.

Batch scanning, inventory modes, scan queues, CSV export, and similar commercial workflows are intentionally out of scope. Those features would be overboard for this app and would add UI bloat, duplicate-handling rules, export/storage decisions, and edge cases that do not fit a simple consumer scanner.

History search and history export are also intentionally out of scope. The history is meant to be a small local convenience list, not a searchable archive or reporting system.

QR code generation is intentionally out of scope. Adding a generator would turn the app from a scanner into a broader QR toolkit, with extra screens, input validation, output/export choices, generated-code storage questions, and more reliability testing.

Product and ISBN lookup are intentionally out of scope. Those features would push the app toward shopping, catalog, or research behavior, and would likely require internet access, external data sources, or extra product-specific UI that does not fit the current local-only scanner design.

ML Kit auto-zoom is intentionally out of scope. Manual pinch zoom is the chosen zoom behavior, and the app will not add automatic zoom suggestions because auto-zoom can feel jumpy or unpredictable while the user is aiming.

Type-specific scanned-result actions are intentionally out of scope. The app will not add separate Wi-Fi, contact, email, phone, SMS, calendar, map, or location action flows because they would add parsing edge cases, Android intent quirks, extra buttons, and more ways for the result panel to feel unreliable or over-engineered.
