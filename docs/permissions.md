# Permissions

QR Code Scanner is intentionally permission-light. The app only asks for the camera permission because live QR and barcode scanning needs camera frames.

## Requested permissions

### Camera

The app requests:

```xml
<uses-permission android:name="android.permission.CAMERA" />
```

This permission is needed for:

- showing the live CameraX preview
- reading camera frames for on-device QR and barcode analysis
- controlling the scanner torch through the active CameraX camera

The manifest also declares that the app requires camera hardware:

```xml
<uses-feature
    android:name="android.hardware.camera"
    android:required="true" />
```

## Permission request flow

When camera permission is missing, the app shows the permission panel and immediately opens Android's camera permission dialog.

The permission button always says `Allow Camera`. Its wording does not change based on permission state:

- after a normal denial, tapping `Allow Camera` requests the Android permission dialog again while Android still allows another request
- after repeated denial causes Android to stop presenting the dialog, tapping the same `Allow Camera` button opens the app's system settings page so camera permission can be enabled manually
- after returning from app settings, scanning starts if camera permission was granted; otherwise the permission panel remains visible

This keeps the normal first-run request automatic while preserving one consistent fallback button instead of switching between `Allow Camera` and a separate settings label.

## Permissions not requested

The app does not request Internet, location, storage, contacts, accounts, notification, or advertising permissions.

Scanned values are handled locally. Scan history is stored locally on the device.

## Permission and camera messages

The app uses the small subtitle/status line under the main title for scanner state messages.

When camera permission is missing or denied:

- the camera preview is hidden
- the subtitle/status line says `Camera permission is needed before scanning.`
- the permission panel says `Camera permission is required to scan QR codes and barcodes. Scanning happens locally on your device.`
- the permission button says `Allow Camera`

When permission is granted and the camera starts normally:

- the subtitle/status line says `Point your camera at a QR code or barcode.`
- the result card says `No QR code or barcode scanned yet.` until a result is found

When a scan result is found:

- the subtitle/status line says `Preview the scanned result below.`
- the result card shows the scanned value

When the camera cannot start:

- the subtitle/status line says `Error: camera could not start on this device.`
- the app keeps the result/action area visible, but scanning cannot continue until the camera issue is resolved
