# Interface

QR Code Scanner uses one main scanner screen with a live camera preview, a bottom content panel, a local history button, and a small flashlight overlay button when the active camera supports flash.

## Text sizes

The current text scale is intentionally simple:

- title: `28sp`
- subtitle/status: `14sp`
- result label: `12sp`
- result text: `16sp`
- history item value: `16sp`
- history item metadata: `12sp`
- footer: `12sp`

The action buttons use the default Material button text sizing.

## Corner radiuses

The scanner screen uses a rounded radius style:

- result card: `16dp`
- history item cards: `16dp`
- action buttons: `16dp`
- flashlight overlay button: circular `48dp` icon button

The result card and action buttons now share the same radius so the bottom scanner panel feels more unified. The result card stays softer against the dark card and live camera preview without returning to the earlier 24dp radius. The flashlight overlay stays circular because it is a 48dp icon button.

## Icon buttons

Icon buttons use a consistent accessible sizing pattern where practical:

- button touch target: `48dp`
- icon bounds: `24dp`
- padding: `12dp`

The flashlight button keeps a circular background because it floats over the live camera preview. The dark circle gives the icon enough contrast against changing camera content and makes the overlay feel intentionally tappable.

The active flashlight state intentionally uses the app accent color instead of adding a separate yellow or orange torch color. That keeps the control aligned with the main scanner actions, avoids making the torch feel like a warning state, and prevents a one-off color from competing with the rest of the interface. The filled active icon uses a dark tint on cyan because it has clearer contrast at the current 24dp icon size than a white fill would.

The title-row history icon stays transparent because it sits inside the bottom content panel on a stable app background. A circle background there would make it feel too much like a second primary floating action instead of a quiet header utility.

The history screen back icon uses the same transparent 48dp icon button pattern. It sits beside the history title and returns to the scanner screen without changing saved history.

The result-card close icon also stays transparent because it sits inside the result card header. A circle background would add visual weight and compete with the scanned result content. The larger 48dp touch target gives it good usability without making the close action visually loud.

The history icon opens saved local scan history. The close icon only clears the currently visible result from the main scanner screen.

## Screen text states

The large title stays visible at the top of the bottom panel. The subtitle/status line stays below it and changes with scanner state.

Before a QR code is found:

- subtitle/status: `Point your camera at a QR code.`
- result card: `No QR code scanned yet.`

After a QR code is found:

- subtitle/status: `Preview the result before opening it.`
- result card: scanned value

Permission and camera failure states are covered in [Permissions](permissions.md).
