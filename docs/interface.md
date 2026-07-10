# Interface

QR Code Scanner uses one main scanner screen with a live CameraX preview, a subtle scan guide overlay, a bottom content panel, a local history button, and a small torch overlay button when the active camera supports flash.

## Text sizes

The current text scale is intentionally simple:

- title: `28sp`
- subtitle/status: `14sp`
- result label: `12sp`, all-caps, bold
- result text: `16sp` enforced monospace
- history item value: `16sp`
- history item metadata: `12sp`
- footer: `12sp`

The history screen reuses the same title and subtitle scale as the main scanner screen. The action buttons use the default Material button text sizing.

## Corner radiuses

The scanner screen uses a rounded radius style:

- result card: `16dp`
- history item cards: `16dp`
- action buttons: `16dp`
- torch overlay button: circular `48dp` icon button

The result card and action buttons share the same radius so the bottom scanner panel feels unified. The torch overlay stays circular because it floats over the camera preview as a compact utility control.

## Scan guide overlay

The scanner screen includes a visual-only aiming guide above the camera preview. It targets 80% of the visible preview width, keeps a `240dp` minimum, and does not use a fixed maximum cap. The final size is still limited by the available camera width, available camera height, and edge padding, so it can shrink on cramped layouts.

The guide is centered inside the visible camera area above the bottom scanner panel. The app treats the top of the bottom panel as the lower edge of the usable camera area, so result, permission, footer, system bar, and bottom inset changes all keep the guide recalculated against the visible preview area.

Guide positioning is recalculated after window inset changes and layout changes on the root view, bottom content panel, guide overlay, and torch button. The torch button does not shrink the guide just because it exists in the top-right corner; it only triggers recalculation when its layout changes.

The guide is declared below the torch button in the visual stack, so the torch button stays visually above the guide if they ever overlap on a cramped screen. The guide is non-clickable, non-focusable, and hidden from accessibility, so it should not intercept taps or accessibility focus intended for the torch button.

The guide uses neutral light gray at partial opacity (`#7FDADADA`, about 50% opacity). The stroke is `5` vector units on a `240x240` viewport with rounded caps and joins. It is visual polish only; the ML analyzer still scans the full camera frame normally.

## Preview zoom

The camera preview supports manual pinch-to-zoom. The gesture is handled directly on the CameraX preview, applies CameraX zoom ratio changes to the active camera, and clamps each update to the camera's reported minimum and maximum zoom ratios.

The app intentionally does not show plus/minus zoom buttons, a zoom slider, a visible zoom label, saved zoom state, or ML Kit auto-zoom. This keeps the scanner screen clean and avoids another control cluster while still letting users zoom in on smaller or more distant QR codes and barcodes.

## Launcher icon

The launcher icon uses Android's adaptive icon structure instead of pointing the manifest directly at a drawable PNG.

The manifest points normal and round icons to mipmap resources. Android 8.0 and newer use the adaptive icon XML wrappers in `mipmap-anydpi-v26`; older launchers use the fallback bitmap wrappers in `mipmap-anydpi`. Both paths use the same foreground PNG stored at `mipmap-nodpi/ic_launcher_foreground.png`.

The adaptive icon background is the same dark app background (`#101316`). The foreground PNG should remain a `512x512` transparent PNG containing only the centered QR glyph in the app accent cyan (`#00BCD4`). It should not include a baked-in dark background, border, outer container, shadow, mask, or rounded-square background.

The current preferred QR glyph size is about `208x207px`, or about 40.6% of the `512x512` canvas width. This is intentionally smaller than the official adaptive icon maximum because QR finder squares are corner-heavy and real launcher masks can crop aggressively.

## Bottom panel layout

The scanner content panel is `wrap_content` and anchored to the bottom of the screen. The footer is the last child inside that panel, so the footer stays at the bottom of the panel above the bottom padding while the rest of the scan UI grows upward from there.

The panel background stays dark and flat, but includes a top-only `1dp` edge at 15% white (`#26FFFFFF`). The edge values are centralized as `@dimen/scanner_panel_top_edge_height` and `@color/scanner_panel_top_edge`. This gives the bottom scanner panel a subtle boundary over dark or busy camera previews without adding a shadow, full border, rounded container, or thicker divider.

## Result card spacing

The result card keeps `20dp` side and bottom padding, but uses `12dp` top padding so the `Scanned Result` header does not feel pushed too far down by the 48dp close icon row.

The result-card close icon reserves its 48dp header slot even when no result is visible. The icon itself becomes invisible when empty, but the reserved space prevents the `Scanned Result` header from shifting horizontally when a result appears or is cleared.

The scanned result value is forced to use Android's monospace typeface in XML and code. On Android 9 and newer, the result text span also uses Android's `Typeface.MONOSPACE` object directly instead of only the `monospace` family name.

The gap between the result header row and result text stays `8dp`. The gap above the Copy, Open, and Share action row stays `16dp`.

## History screen spacing

The history screen uses the same `20dp` left and right gutters as the scanner panel. Its base bottom padding is also `20dp`, with the bottom system-bar inset added on devices that need it.

The history subtitle sits `8dp` below the title row. When history has saved items, the list starts `20dp` below the subtitle. When history is empty, the empty-state text sits `24dp` below the subtitle. History cards are separated by `8dp`, and the Clear All History button sits `20dp` below the list area.

## Icon buttons

Icon buttons use a consistent accessible sizing pattern where practical:

- button touch target: `48dp`
- icon bounds: `24dp`
- padding: `12dp`

The torch button follows this pattern with a `48dp` circular button and `12dp` padding around the flash icon. It uses normal Android click activation instead of custom touch-down handling. This keeps the control aligned with standard button behavior while still toggling the active CameraX torch and triggering haptic feedback after a valid toggle request.

The torch button floats at the top-end of the camera preview with a `24dp` top margin and `20dp` end margin. Runtime system-bar and display-cutout insets are added to those margins, so the control remains safe-area-aware on different devices.

The torch button keeps a circular background because it floats over the live camera preview. The inactive background uses 40% black (`#66000000`) so the icon remains visible over bright camera content without making the off state too heavy. The active state uses opaque cyan (`#00BCD4`) with a dark filled icon for clearer contrast.

The title-row history icon, result-card close icon, and history-screen back icon all use the shared secondary icon-button pattern: a `48dp` button, a `24dp` vector icon, `12dp` padding, dim white tint, and the shared bounded oval ripple.

The history icon opens saved local scan history. The back icon only leaves the history screen. The close icon only clears the currently visible result from the main scanner screen and does not clear saved scan history.

## Screen text states

The large title stays visible at the top of the bottom panel. The subtitle/status line stays below it and changes with scanner state.

Before a scan result is found:

- subtitle/status: `Point your camera at a QR code or barcode.`
- result card: `No QR code or barcode scanned yet.`

After a scan result is found:

- subtitle/status: `Preview the scanned result below.`
- result card: scanned value

When camera permission is missing or denied:

- subtitle/status: `Camera permission is needed before scanning.`

When the camera cannot start:

- subtitle/status: `Error: camera could not start on this device.`

Permission details are covered in [Permissions](permissions.md).
