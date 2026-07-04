# Scope

QR Code Scanner is a small local scanner for QR codes and common barcodes.

## Supported formats

The scanner currently enables these formats:

- QR Code
- EAN-13
- EAN-8
- UPC-A
- UPC-E
- Code 128
- Code 39
- Code 93
- Codabar

These cover the app's current target: QR codes plus common retail and linear barcode formats.

## Not enabled yet

The app does not currently enable every barcode family. These formats are intentionally out of scope for now:

- PDF417
- Aztec
- Data Matrix
- ITF
- less common specialized formats not listed above

Keeping the enabled list narrow avoids turning on broad all-format scanning before real device testing shows that it is useful.

## Result handling

All accepted scan results use the same simple result flow:

- preview the scanned value
- copy it
- share it
- open it only when it looks like a safe web link
- save it in local scan history

Scanning happens locally on the device. The app requests only camera permission.
