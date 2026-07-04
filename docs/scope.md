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

These formats are skipped for practical scope reasons:

- PDF417 is common for IDs, shipping labels, and larger stacked codes, but it is less central to the current simple QR and retail-barcode use case.
- Aztec and Data Matrix are compact 2D formats used in some transport, logistics, and industrial contexts, but they expand the app beyond the current consumer scanner target.
- ITF is often used for packaging and distribution codes, but it is less useful for ordinary QR and retail scanning than EAN, UPC, and Code formats.
- Less common specialized formats should be added only when there is a clear reason to support them and enough device testing to confirm they do not make normal scanning noisier.

Keeping the enabled list narrow avoids turning on broad all-format scanning before real device testing shows that it is useful. The current approach also makes the app behavior easier to explain: it scans QR codes and common retail or linear barcodes, not every barcode family ML Kit can recognize.

## Result handling

All accepted scan results use the same simple result flow:

- preview the scanned value
- copy it
- share it
- open it only when it looks like a safe web link
- save it in local scan history

Scanning happens locally on the device. The app requests only camera permission.
