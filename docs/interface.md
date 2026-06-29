# Interface

QR Code Scanner uses one main scanner screen with a live camera preview, a bottom content panel, and a small flashlight overlay button when the active camera supports flash.

## Text sizes

The current text scale is intentionally simple:

- title: `28sp`
- subtitle/status: `15sp`
- result label: `12sp`
- result text: `16sp`
- footer: `12sp`

The action buttons use the default Material button text sizing.

## Corner radiuses

The scanner screen uses a mostly squarish radius style:

- result card: `8dp`
- action buttons: `16dp`
- flashlight overlay button: `24dp`

The result card was kept tighter to match the broader ThinApps preference for less bubbly surfaces. The action buttons remain more rounded so they still read clearly as tappable Material controls. The flashlight overlay stays circular because it is a 48dp icon button.

## Screen text states

The large title stays visible at the top of the bottom panel. The subtitle/status line stays below it and changes with scanner state.

Before a QR code is found:

- subtitle/status: `Point your camera at a QR code.`
- result card: `No QR code scanned yet.`

After a QR code is found:

- subtitle/status: `Preview the result before opening it.`
- result card: scanned value

Permission and camera failure states are covered in [Permissions](permissions.md).
