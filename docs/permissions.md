# Permissions

QR Code Scanner is intentionally permission-light. The app only asks for the camera permission because live QR scanning needs camera frames.

## Requested permissions

### Camera

The app requests:

```xml
<uses-permission android:name="android.permission.CAMERA" />
```

This permission is needed for:

- showing the live CameraX preview
- reading camera frames for on-device QR analysis
- controlling the scanner flashlight through the active CameraX camera

The manifest also declares that the app requires camera hardware:

```xml
<uses-feature
    android:name="android.hardware.camera"
    android:required="true" />
```

## Permissions not requested

The app does not request Internet, location, storage, contacts, accounts, notification, or advertising permissions.

Scanned values are handled locally. The app does not upload scans, keep scan history, or send scanned content to a server.

## Permission and camera messages

The app uses the small subtitle/status line under the main title for scanner state messages.

When camera permission is missing or denied:

- the camera preview is hidden
- the subtitle/status line says `Camera permission is needed before scanning can start.`
- the permission panel says `Camera permission is required to scan QR codes. Scanning happens locally on your device.`
- the permission button says `Allow Camera Permission`

When permission is granted and the camera starts normally:

- the subtitle/status line says `Point your camera at a QR code.`
- the result card says `No QR code scanned yet.` until a QR code is found

When a QR code is found:

- the subtitle/status line says `Preview the result before opening it.`
- the result card shows the scanned value

When the camera cannot start:

- the subtitle/status line says `Camera could not start on this device.`
- the app keeps the result/action area visible, but scanning cannot continue until the camera issue is resolved
