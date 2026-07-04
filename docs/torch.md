# Scanner Torch Behavior

QR Code Scanner uses CameraX for the live camera preview and frame analysis. Because the camera is already open and owned by the scanner pipeline, the flashlight toggle also stays inside CameraX by calling `camera.cameraControl.enableTorch(...)` on the active CameraX camera.

This is intentionally different from a standalone flashlight app. A flashlight-only app can often use Android's lower-level `CameraManager.setTorchMode(...)` API directly because it does not need to keep a camera preview and image analyzer open at the same time. That path can feel faster on some devices, but it is not the safest default for this app because the scanner already has the camera in use.

Android documents `CameraManager.setTorchMode(...)` as a way to control torch mode without opening the camera device, but it can fail when the camera device or camera resources are already in use. CameraX provides `CameraControl.enableTorch(...)` for controlling the torch on the active CameraX camera, which fits this app's preview-first scanning model.

## Current approach

- The app binds a CameraX preview and image analyzer to the back camera.
- The flashlight button is shown only when the active CameraX camera reports a flash unit.
- Tapping the flashlight button calls CameraX `enableTorch(...)` on that active camera.
- The button updates immediately so the interface feels responsive while CameraX applies the torch request.
- When the flashlight is off, the button uses a 40% black (`#66000000`) circular background and outline-style flash icon.
- When the flashlight is on, the button uses an opaque cyan (`#00BCD4`) circular background, a dark foreground tint, and a filled flash icon.
- The app observes CameraX torch state and syncs the button back to the real camera state.
- If CameraX rejects a torch request, the app logs the failure and syncs the button back to the current torch state.

## Visual state rationale

The active flashlight button keeps the app's cyan accent instead of adding a separate yellow or orange torch color. The flashlight is a scanner utility control, not the app's main brand element, so reusing the existing accent keeps the interface consistent with the Material action buttons and avoids introducing a second attention color that could read as warning.

The inactive flashlight background is translucent so the floating overlay still feels lightweight over the live camera preview. The active flashlight background is intentionally opaque so the on state is clear and does not fade into bright camera content.

The flashlight button drawable keeps its base oval fully opaque while runtime background tint supplies the inactive or active color. This prevents the active cyan tint from inheriting the inactive overlay alpha on devices that preserve alpha through background tinting.

The active icon uses a dark tint instead of white because the active background is already bright cyan. A dark filled icon gives clearer small-icon contrast on that background, especially at the current 24dp icon size.

## Why not copy Bright Flashlight exactly?

Bright Flashlight is a dedicated flashlight utility. Its main torch controller can choose a flash-capable camera ID and call `CameraManager.setTorchMode(...)` directly because it is not running a live camera preview and ML analyzer.

QR Code Scanner is different: the camera is already bound for scanning. Mixing an independent `CameraManager.setTorchMode(...)` call into that active CameraX camera session may be faster on some devices, but it is more likely to hit device-specific failures or conflicts.

## Future improvements

If users report that scanner flashlight behavior still feels slow or inconsistent on specific devices, revisit the interaction after testing. The default should remain CameraX-first unless there is a clear reason to risk a lower-level torch path.

Relevant Android docs:

- CameraX `CameraControl.enableTorch(...)`: https://developer.android.com/reference/androidx/camera/core/CameraControl#enableTorch(boolean)
- CameraX `CameraInfo.getTorchState()`: https://developer.android.com/reference/androidx/camera/core/CameraInfo#getTorchState()
- Camera2 `CameraManager.setTorchMode(...)`: https://developer.android.com/reference/android/hardware/camera2/CameraManager#setTorchMode(java.lang.String,%20boolean)
