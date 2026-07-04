# Scope

QR Code Scanner is a small local scanner for QR codes and common barcode formats supported by ML Kit.

## Supported formats

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

## Practical scan notes

Some supported formats are more demanding than others:

- EAN and UPC are the main retail product formats and should be part of any normal barcode scanner.
- Code 128, Code 39, Code 93, Codabar, and ITF cover common linear formats used across packaging, inventory, labels, libraries, and older systems.
- Aztec is useful for some ticketing and transport codes, so it is enabled even though QR is more familiar to most users.
- PDF417 can be large and dense. It may need a clearer, closer, or higher-resolution camera view than simple retail barcodes.
- Data Matrix is useful for compact 2D labels, but ML Kit only recognizes Data Matrix codes that intersect the center point of the input image.

## Still out of scope

The app does not support barcode families that ML Kit's current barcode scanner does not expose, such as:

- Micro QR or rectangular Micro QR
- MicroPDF417
- MaxiCode
- DotCode
- postal barcodes
- MSI, Plessey, Code 11, Pharmacode, or similar legacy/specialized linear codes
- proprietary color, circular, or app-specific codes

Those formats are not part of the current app scope. Adding them would require a different scanner library, custom decoding, or a clearer use case than this small local scanner currently targets.

## Result handling

All accepted scan results use the same simple result flow:

- preview the scanned value
- copy it
- share it
- open it only when it looks like a safe web link
- save it in local scan history

Scanning happens locally on the device. The app requests only camera permission.
