# Permissions

QR Code Scanner is intentionally permission-light. The app asks only for camera permission, and only because live QR and barcode scanning needs camera frames. Selecting one existing image uses Android's system Photo Picker and does not require storage or broad photo-library permission.

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

Camera permission is not used for selected-image scanning.

## Android Photo Picker access

The title-row photo-library icon launches Android's standard single-image Photo Picker with an image-only request.

The app receives temporary access only to the selected picker URI. ML Kit reads and processes that URI locally through its standard file-path image handling, and the app does not copy or retain the selected image in app storage.

The picker can remain available when camera permission is denied or the camera cannot start. Canceling the picker grants nothing and changes nothing in the app.

No `READ_EXTERNAL_STORAGE`, `READ_MEDIA_IMAGES`, or broad photo-library permission is requested. The system picker also provides the compatible fallback path on Android versions where the newer picker interface is unavailable.

## Permission request flow

When camera permission is missing, the app shows the permission panel and immediately opens Android's camera permission dialog.

The permission button always says `Allow Camera`. Its wording does not change based on permission state:

- after a normal denial, tapping `Allow Camera` requests the Android permission dialog again while Android still allows another request
- after repeated denial causes Android to stop presenting the dialog, tapping the same `Allow Camera` button opens the app's system settings page so camera permission can be enabled manually
- after returning from app settings, live scanning starts if camera permission was granted; otherwise the permission panel remains visible

This keeps the normal first-run request automatic while preserving one consistent fallback button instead of switching between `Allow Camera` and a separate settings label. The photo-library icon remains a separate permission-free scanning option.

## Permissions not requested

The app does not request Internet, location, storage, media-library, contacts, accounts, notification, or advertising permissions.

No Internet permission means the scanner itself does not upload camera frames, selected images, scanned values, or history; call remote decoding, OCR, AI, search, reputation, or lookup services; fetch webpages; or perform background web research. All scanning and link-shape validation run on the device.

The user can still explicitly tap `Open` to hand a supported link to another installed app, or tap `Share` to invoke Android's share sheet. Those actions are handled outside the scanner through normal Android intents and do not require the scanner to hold Internet permission.

Camera frames, selected images, and scanned values are processed locally. Scan history stores only the accepted value and timestamp in private app preferences. Selected image files are not stored by the app.

The main scanner footer includes a `Privacy Policy` text link. It opens an in-app modal explaining this local processing, local history storage, and the user-initiated `Open` and `Share` actions. The modal does not load a webpage and does not require Internet permission.

## Permission and camera messages

The app uses the small subtitle/status line under the main title for scanner state messages.

When camera permission is missing or denied:

- the camera preview is hidden
- the subtitle/status line says `Camera permission is needed before scanning.`
- the permission panel says `Camera permission is required to scan QR codes and barcodes. Scanning happens locally on your device.`
- the permission button says `Allow Camera`
- the photo-library icon remains enabled

When permission is granted and the camera starts normally:

- the subtitle/status line says `Point your camera at a QR code or barcode.`
- the result card says `No QR code or barcode scanned yet.` until a result is found

When a camera or selected-image result is found:

- the subtitle/status line says `Preview the scanned result below.`
- the result card shows the scanned value

When the camera cannot start:

- the subtitle/status line says `Error: camera could not start on this device.`
- the app keeps the result/action area and photo-library icon available even though live scanning cannot continue
