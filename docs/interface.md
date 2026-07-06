# Interface

QR Code Scanner uses one main scanner screen with a live camera preview, a subtle scan guide overlay, a bottom content panel, a local history button, and a small flashlight overlay button when the active camera supports flash.

## Text sizes

The current text scale is intentionally simple:

- title: `28sp`
- subtitle/status: `14sp`
- result label: `12sp`, all-caps, bold
- result text: `16sp` enforced monospace
- history item value: `16sp`
- history item metadata: `12sp`
- footer: `12sp`

The history screen reuses the same `28sp` title size and `14sp` subtitle/hint size as the main scanner screen. Its title starts after the 48dp back button slot, so the alignment differs from the main scanner title, but the text scale stays the same.

The action buttons use the default Material button text sizing.

## Corner radiuses

The scanner screen uses a rounded radius style:

- result card: `16dp`
- history item cards: `16dp`
- action buttons: `16dp`
- flashlight overlay button: circular `48dp` icon button

The result card and action buttons now share the same radius so the bottom scanner panel feels more unified. The result card stays softer against the dark card and live camera preview without returning to the earlier 24dp radius. The flashlight overlay stays circular because it is a 48dp icon button.

## Scan guide overlay

The scanner screen includes a visual-only aiming guide above the camera preview. It targets 80% of the visible preview width, keeps a `240dp` minimum, and does not use a fixed maximum cap. The final size is still limited by the available camera width, available camera height, and edge padding, so it can shrink on cramped layouts without being artificially capped on larger screens. The guide uses four open corners instead of a full box, so it gives users an aiming reference without making the camera view feel boxed in.

The guide is dynamic. It is centered inside the visible camera area above the bottom scanner panel, not inside the full screen. The app treats the top of the bottom scanner panel as the lower edge of the usable camera area, so if the result card, permission section, footer, system bars, or bottom inset changes the panel height, the guide is recalculated and re-centered in the remaining camera space.

The guide positioning is recalculated after window inset changes and layout changes on the root view, bottom content panel, guide overlay, and torch button. The torch button no longer shrinks the guide just because it exists in the top-right corner; it only triggers recalculation when its layout changes. This keeps the guide larger and centered while still adapting across different screen heights, bottom panel sizes, system bars, and cutout configurations.

The guide uses a neutral light gray at partial opacity (`#8CDADADA`) instead of the app accent cyan. This keeps it feeling like passive camera UI rather than a branded action or scan-result state. It is non-clickable, hidden from accessibility, and does not change how detection works. The ML analyzer still scans the camera frame normally; the guide is only visual polish.

## Launcher icon

The launcher icon uses Android's adaptive icon structure instead of pointing the manifest directly at a `drawable` PNG.

The manifest points to mipmap resources:

- `android:icon="@mipmap/ic_launcher"`
- `android:roundIcon="@mipmap/ic_launcher_round"`

Android 8.0 and newer use the adaptive icon XML wrappers in:

- `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`
- `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`

Both adaptive wrappers define the two required color icon layers:

- background: `@color/ic_launcher_background`
- foreground: `@mipmap/ic_launcher_foreground`

The background color is the same dark app background (`#101316`). The foreground is the single QR glyph PNG stored at:

- `app/src/main/res/mipmap-nodpi/ic_launcher_foreground.png`

Older devices use bitmap fallback wrappers in:

- `app/src/main/res/mipmap-anydpi/ic_launcher.xml`
- `app/src/main/res/mipmap-anydpi/ic_launcher_round.xml`

Those fallback wrappers point to the same foreground PNG so the repo keeps one launcher PNG source while still exposing the icon through `mipmap` resources.

Google's adaptive icon guidance says color adaptive icons should have separate foreground and background layers, the full icon layers are `108x108dp`, the central safe zone is `66x66dp`, the visible logo should be at least `48x48dp` and no larger than `66x66dp`, and the outer `18dp` on each side is reserved for masks and visual effects. Google also says layers should have clean edges and should not include masks or background shadows around the icon outline.

For this app, the QR glyph needs a more conservative target than the official maximum because the QR finder squares are corner-heavy and real launcher masks can crop aggressively. A previous foreground around `59%` of the `512x512` canvas was too large in adaptive icon testing. The current preferred foreground target is about `208x207px`, or about `40.6%` of the canvas width, centered with about `152px` left and right padding and about `152px` top and bottom padding.

The foreground PNG should stay:

- `512x512` PNG
- real RGBA transparency around the QR glyph
- QR glyph only, with no baked-in dark background
- centered on the canvas
- visible glyph around `208x207px`
- app accent cyan (`#00BCD4`)
- free of extra borders, outer containers, shadows, masks, or rounded-square backgrounds

This approach matches Android launcher icon conventions better than a direct `@drawable` PNG. It gives modern launchers the foreground/background layers they expect for icon shapes, keeps the important QR marks padded away from clipping, avoids maintaining separate density PNG sets for this small app, and makes future icon refreshes simple: replace only `app/src/main/res/mipmap-nodpi/ic_launcher_foreground.png` with a transparent foreground PNG that follows the same sizing and color rules.

## Bottom panel layout

The scanner content panel is `wrap_content` and anchored to the bottom of the screen. The footer is the last child inside that panel, so the footer stays at the bottom of the panel above the bottom padding while the rest of the scan UI grows upward from there.

This keeps the bottom scanner area visually bottom-aligned instead of floating in the middle of the preview. Android still lays out the panel children from top to bottom internally, but the panel's final height is determined by its contents and anchored from the bottom edge.

The panel background stays dark and flat, but now includes a subtle 1dp top edge at 15% white (`#26FFFFFF`). This gives the panel a visible boundary over dark camera previews without adding a shadow or full border.

## Result card spacing

The result card keeps `20dp` side and bottom padding, but uses `12dp` top padding so the `Scanned Result` header does not feel pushed too far down by the 48dp close icon row.

The `Scanned Result` label uses `12sp` text, all-caps styling, bold weight, and slight letter spacing so it reads as a quiet section label rather than another body line.

The result-card close icon reserves its 48dp header slot even when no result is visible. The icon itself becomes invisible when empty, but the reserved space prevents the `Scanned Result` header from shifting horizontally when a result appears or is cleared.

The scanned result value is forced to use Android's monospace typeface in XML and code. On Android 9 and newer, the result text span also uses Android's `Typeface.MONOSPACE` object directly instead of only the `monospace` family name. This gives URLs, codes, and other scanned values clearer structure without adding a bundled font file, nested background, or heavier result card styling.

The gap between the result header row and result text stays `8dp`. The gap above the Copy, Open, and Share action row stays `16dp`.

## History screen spacing

The history screen uses the same `20dp` left and right gutters as the scanner panel. Its base bottom padding is also `20dp`, with the bottom system-bar inset added on devices that need it.

The history subtitle sits `8dp` below the title row. When history has saved items, the list starts `20dp` below the subtitle. When history is empty, the empty-state text sits `24dp` below the subtitle so the empty message has a little more breathing room.

History cards are separated by `8dp` after the first item. The Clear All History button sits outside the scroll area and uses a `20dp` top margin, matching the list's `20dp` section gap below the subtitle.

## Icon buttons

Icon buttons use a consistent accessible sizing pattern where practical:

- button touch target: `48dp`
- icon bounds: `24dp`
- padding: `12dp`

The flashlight button keeps a circular background because it floats over the live camera preview. The inactive background uses 40% black (`#66000000`) so the icon remains visible over bright camera content without making the off state too heavy.

The active flashlight state intentionally uses opaque cyan (`#00BCD4`) instead of adding a separate yellow or orange torch color. That keeps the control aligned with the main scanner actions, avoids making the torch feel like a warning state, and prevents a one-off color from competing with the rest of the interface. The filled active icon uses a dark tint on cyan because it has clearer contrast at the current 24dp icon size than a white fill would.

The flashlight button drawable uses a fully opaque base oval and relies on runtime background tint for the actual off or on color. This keeps the inactive state translucent while allowing the active cyan state to stay fully opaque.

The title-row history icon, result-card close icon, and history-screen back icon use the same bounded circular ripple with 5% white (`#0DFFFFFF`) for press feedback. This gives their 48dp touch targets a visible response without adding resting visual weight or making any secondary icon action compete with the flashlight overlay.

The title-row history icon stays transparent at rest because it sits inside the bottom content panel on a stable app background. A permanent circle background there would make it feel too much like a second primary floating action instead of a quiet header utility.

The history screen back icon stays visually quiet for the same reason. It sits beside the history title, returns to the scanner screen, and does not change saved history.

The result-card close icon intentionally has no permanent round background. It sits inside the result card header, so a resting circle would add visual weight, make the clear action look too primary, and compete with the scanned result content. The 48dp touch target and shared 5% white ripple keep it usable without making the action visually loud.

The history icon opens saved local scan history. The close icon only clears the currently visible result from the main scanner screen.

## Screen text states

The large title stays visible at the top of the bottom panel. The subtitle/status line stays below it and changes with scanner state.

Before a scan result is found:

- subtitle/status: `Point your camera at a QR code or barcode.`
- result card: `No QR code or barcode scanned yet.`

After a scan result is found:

- subtitle/status: `Preview the scanned result.`
- result card: scanned value

Permission and camera failure states are covered in [Permissions](permissions.md).
