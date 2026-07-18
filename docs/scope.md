# Scope

QR Code Scanner is a small local scanner for QR codes and common barcode formats supported by ML Kit. It accepts either the live rear camera or one image selected through Android's Photo Picker.

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

Both supported scan sources use this same format list:

- continuous live scanning from the fixed rear camera
- one image selected through Android's standard single-image Photo Picker

Selected-image scanning first performs one bounded decode so neither bitmap side exceeds `2048` pixels, applies supported EXIF orientation metadata, then accepts the first non-blank raw value only and preserves it without trimming or rewriting it. It does not add cropping, editing, image preview, multi-select, batch processing, or a separate result flow.

## Supported with practical caveats

Some supported formats are more demanding than others:

- EAN and UPC are the main retail product formats and should be part of any normal barcode scanner.
- Code 128, Code 39, Code 93, Codabar, and ITF cover common linear formats used across packaging, inventory, labels, libraries, and older systems.
- Aztec is useful for some ticketing and transport codes, so it is enabled even though QR is more familiar to most users.
- PDF417 can be large and dense. It may need a clearer, closer, or higher-resolution camera view or selected image than simple retail barcodes.
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

All accepted camera and selected-image results use the same simple result flow:

- preview the scanned value
- copy it
- share it
- open it only when it passes the app's local web-link checks
- save it in local scan history

The accepted non-blank raw value is preserved exactly across the result card, Copy, Share, transient result restoration, and local history. The app does not trim, edit, normalize, or otherwise rewrite it; URL normalization is used only when the user explicitly opens a supported web link.

The app does not label whether a saved result came from the camera or an image. History remains timestamp-only and keeps one row per exact value.

The Open action uses minimal local parsing checks. It accepts only HTTP and HTTPS web links, rejects embedded whitespace, backslashes, control characters, Unicode bidirectional-control characters, URL user-info, missing hosts, and unsupported explicit schemes, and applies stricter hostname-label checks when adding HTTPS to a result without a scheme. These checks do not detect phishing, consult an online reputation service, or verify that the destination itself is trustworthy.

Scanning happens locally on the device. Camera permission is requested only for live scanning. Selected-image scanning uses the system picker, requires no storage or broad photo permission, performs one bounded local decode with supported EXIF orientation applied, and does not copy or retain the selected image in app storage.

## Back burner

Translations are not part of the current release scope, but they are not ruled out. The app stays English-only for now while its features and wording are still settling.

Localization can be reconsidered if the app grows enough to justify maintaining translated string resources, preferably with reliable native or community review rather than unverified machine translation. Any future translation work should cover the complete visible interface and accessibility labels instead of shipping partial language support.

Showing the detected barcode format, such as `QR Code`, `EAN-13`, or `Code 128`, is also on the back burner rather than ruled out. This information may be useful to technical users, but most people may not recognize the terminology and an extra label could make the result card feel more complicated.

Barcode-format labels should be reconsidered only if users request them or testing shows a clear benefit. They would identify the barcode symbology only and must not be confused with content-type labels such as Website, Wi-Fi, Product, or Contact, which remain intentionally out of scope.

## Intentionally out of scope

The app is not trying to be a warehouse, inventory, enterprise scanning tool, shopping lookup app, QR creation toolkit, image editor, or multi-purpose QR action launcher.

Batch scanning, inventory modes, scan queues, CSV export, and similar commercial workflows are intentionally out of scope. Those features would be overboard for this app and would add UI bloat, duplicate-handling rules, export/storage decisions, and edge cases that do not fit a simple consumer scanner.

Selected-image scanning stays deliberately narrow. The app will not add multi-image selection, batch image scanning, crop or rotate tools, an image editor, retained image previews, saved image copies, or a custom gallery screen. The standard Photo Picker provides the one approved image entry point.

Selected-image fallback preprocessing is also intentionally out of scope. The initial bounded decode and EXIF orientation correction are the complete normal preparation path. The app will not retry alternate mirrored copies, inverted-color copies, contrast or brightness adjustments, sharpening, thresholding, extra rescaling passes, or other transformed versions after scanning fails. Multiple fallback transformations and decoder passes would add memory use, delay, branches, and testing for rare edge cases. One specific fallback should be reconsidered only if repeated real-world reports prove it is needed.

PDFs, office documents, and arbitrary non-image files are intentionally unsupported. Supporting them would require a broader file picker, PDF or document rendering, page selection, file-type handling, and additional failure states that do not fit the app's simple camera-or-image model.

A Share-sheet image receiver is intentionally not included. The existing Photo Picker already covers scanning saved images from inside the app. Adding a receiver would provide only a small shortcut while adding another exported manifest entry point, cold-launch and already-running intent handling, temporary shared-URI permission cases, invalid or missing shared content, and more lifecycle and testing surface. Images therefore cannot be shared into the app unless future user demand clearly justifies that extra complexity.

External scanner integration is intentionally out of scope. Other apps cannot launch this app through a custom scanner intent, deep link, activity-result contract, or compatibility API and receive the scanned value back. That would create an exported integration contract with caller validation, cancellation and error behavior, external launch states, and long-term compatibility requirements.

Remote scanning, online research, and cloud-assisted analysis are intentionally out of scope. The app will not upload camera frames, selected images, or scanned values to a server, cloud function, remote decoder, OCR service, AI service, search engine, reputation service, product database, or other lookup provider. It will not fetch webpages, research scanned content, perform reverse-image searches, or run background web checks. Scanning and validation remain on-device; tapping `Open` merely hands an approved web link to another installed app chosen by Android.

URL destination analysis and safety scoring are intentionally out of scope. The app will not guess a registrable domain, maintain public-suffix or TLD rules, classify country or specialized TLDs, add an `Opens: example.com` summary, or label a link Safe or Suspicious. Modern domain structures are too varied for a small local heuristic to be consistently reliable, and mistakes could either flag valid links or give users false confidence. The app instead shows the exact scanned value and uses only basic structural checks to decide whether `Open` may be enabled.

Manual text entry and automatic clipboard changes are intentionally out of scope. Results come only from the live camera or one selected image; the app will not add an editable result field, Paste button, clipboard import, manually typed URL or text entry, automatic copying, or an auto-copy preference. The result remains read-only and Copy remains an explicit user action.

History search, history export, filters, folders, favorites, labels, pinning, tagging, editing, notes, cloud sync, saved collections, history on/off settings, private scanning modes, retention settings, and per-item deletion are intentionally out of scope. The app does have a history screen, but it is meant to stay a small local recent-results list with Clear All History as its only management action, not a searchable archive, organizer, configurable privacy system, reporting tool, or saved-items database.

QR code generation is intentionally out of scope. Adding a generator would turn the app from a scanner into a broader QR toolkit, with extra screens, input validation, output/export choices, generated-code storage questions, and more reliability testing.

Product and ISBN lookup are intentionally out of scope. Those features would push the app toward shopping, catalog, or research behavior, and would likely require internet access, external data sources, or extra product-specific UI that does not fit the current local-only scanner design.

ML Kit auto-zoom is intentionally out of scope. Manual pinch zoom is the chosen zoom behavior, and the app will not add automatic zoom suggestions because auto-zoom can feel jumpy or unpredictable while the user is aiming.

Forced keep-awake behavior is intentionally out of scope. The app will follow the device's normal screen timeout and will not use a keep-screen-on flag, wake lock, or keep-awake setting. The live camera preview and barcode analyzer already consume noticeable battery, so preventing normal timeout could leave them running unnecessarily when the phone is set down or the app is forgotten.

Dedicated device-class layouts are intentionally out of scope. The app should remain reasonably usable across common orientations, screen sizes, tablets, foldables, and Android multi-window use, and actual overlap, cutoff, inset, scrolling, or camera-layout problems should be fixed as normal reliability bugs. It will not add separate landscape, tablet, foldable, split-screen, two-column, or hinge-aware layouts unless real device testing finds a compatibility problem that cannot be fixed within the shared layout.

Type-specific scanned-result actions are intentionally out of scope. The app will not add separate Wi-Fi, contact, email, phone, SMS, calendar, map, or location action flows because they would add parsing edge cases, Android intent quirks, extra buttons, and more ways for the result panel to feel unreliable or over-engineered.

Content-type result labels are intentionally out of scope. The app will not label results as Website, Text, Email, Phone, Product, Wi-Fi, Contact, Calendar, Location, or similar categories because inaccurate labels, ambiguous payloads, and parser edge cases could hurt trust more than they help.

Scan lock, capture lock, and Scan Again flows are intentionally out of scope. The app keeps live scanning active and uses the result gate to reduce duplicate noise instead of adding another scanner state, another large button, or an extra tap before scanning can continue.

Automatic result actions are intentionally out of scope. The app will not auto-open links, launch another app immediately after scanning, or add an auto-open preference. Users always review the scanned value first and explicitly choose `Open` when available.

Audio scan feedback is intentionally out of scope. The app will not add a scan beep, success sound, spoken confirmation, or sound setting. Accepted scans already use haptic feedback and visible result feedback without making noise in public or quiet places.

Extra scanner controls are intentionally out of scope. The app will not add plus/minus zoom buttons, a zoom slider, a separate focus button, a visible focus ring, or a front-camera switch. The rear camera is the fixed scanner camera because it is the normal choice for aiming at nearby codes and generally provides better close-range focus, detail, and low-light performance. Pinch zoom and quiet CameraX tap-to-focus are the complete manual camera controls for now.

Theme customization is intentionally out of scope. The app will keep one dark interface instead of adding light mode, alternate themes, accent pickers, or a theme settings screen.

Secondary launcher surfaces are intentionally out of scope. The app will not add a Quick Settings tile, lock-screen shortcut, persistent notification shortcut, home-screen widget, or similar entry point because the normal launcher already opens directly into the scanner.
