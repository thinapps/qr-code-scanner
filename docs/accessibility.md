# Accessibility

QR Code Scanner uses standard Android Views and Material controls and keeps custom accessibility behavior limited. The app should rely on Android's built-in accessibility support where it already works, then add special behavior only when manual testing shows a real problem.

This document describes the current baseline, choices that are intentionally not implemented, and items to verify before future accessibility changes. It does not claim that the app has completed a formal accessibility audit or that the camera-scanning experience is fully usable without vision.

## Current baseline

The current interface already includes these accessibility basics:

- primary action buttons use standard Material buttons
- icon buttons use `48dp` touch targets with `24dp` icons
- the history, back, clear-result, and torch icon buttons have descriptive string-resource labels
- the torch label changes between `Turn torch on` and `Turn torch off`, so its state is not communicated only by color or icon shape
- unavailable result actions are disabled through normal Android button state
- visible text remains normal `TextView` content so Android accessibility services can announce it without duplicate content descriptions
- the scanned result is selectable text and its Copy, Open, and Share actions remain separate standard buttons
- accepted scans and valid button actions provide haptic feedback
- the visual scan guide is non-clickable, non-focusable, and excluded from accessibility because it is decorative
- the Android camera permission dialog uses the system's own accessibility behavior
- the fallback permission control keeps the visible and announced label `Allow Camera`

The app's core scanning flow still depends on physically aiming the camera at a code. The current success haptic confirms an accepted scan, but the app does not provide nonvisual aiming guidance.

## Not implemented for now

### Automatic status announcements

The scanner status line does not use `android:accessibilityLiveRegion` and the app does not call `announceForAccessibility()` whenever the status changes.

The normal ready and found-result messages can change during scanning. Automatically announcing every change could interrupt TalkBack users or repeat information without reading the actual result. The system camera permission dialog already announces itself.

A targeted announcement should be considered only if manual testing shows that a critical state, such as a camera-start failure, is otherwise missed.

### Automatic reading of scanned values

The app does not automatically speak the full scanned value after every scan. Scanned values can be long, sensitive, malformed, or intentionally disruptive. Repeated detections could also cause unwanted interruptions.

The result remains visible and selectable so users can move accessibility focus to it when they choose.

### Automatic focus movement

The app does not force accessibility focus from the current control to the result card after every accepted scan. Repeated scans must not hijack navigation or trap the user in a focus loop.

Focus movement should be added only if TalkBack testing shows that users cannot discover a newly scanned result through normal navigation.

### Special scanning mode

The app does not add:

- a custom accessibility service
- a separate screen-reader mode
- spoken aiming directions
- continuous proximity tones
- vibration-based left, right, up, or down guidance
- automatic zoom solely for accessibility

Reliable nonvisual camera aiming would be a substantial feature, not a small accessibility flag. It should not be added without a clear design and real testing.

### Redundant descriptions and custom actions

The app does not add content descriptions to ordinary text labels because Android already announces visible `TextView` text. It also does not replace standard Material button actions with custom accessibility actions unless testing shows that a control's purpose is unclear.

The scan guide and other decorative graphics must stay excluded from accessibility navigation.

## Testing before future changes

Accessibility changes should be driven by testing rather than added speculatively. At minimum, test these flows with TalkBack:

- first launch and camera permission approval
- normal permission denial and retry
- repeated denial followed by the `Allow Camera` settings fallback
- torch discovery and on/off state
- accepted scan feedback
- result discovery and reading
- Copy, Open, Share, and clear-result actions
- opening history, selecting a saved result, and clearing history
- camera-start failure behavior when it can be reproduced

Also verify:

- Switch Access or keyboard focus can reach every action in a sensible order
- large font settings do not hide controls or make the bottom panel unusable
- dim text and disabled states remain distinguishable with adequate contrast
- the Accessibility Scanner and Android Lint do not report meaningful unresolved issues
- each history card is announced with enough context to understand its value, timestamp, and tap action

## Possible future improvements

These are reasonable only after testing demonstrates a need:

- mark screen titles as accessibility headings if heading navigation improves the two-screen interface
- group each history value and timestamp into one screen-reader focus unit if the current card announcement is fragmented
- add a clearer accessibility click label for history cards if `Double tap to activate` does not explain that the item previews the saved result
- add a targeted announcement for a camera-start error if the visible status text is not discovered
- adjust focus order if TalkBack, Switch Access, or keyboard navigation reaches controls in a confusing sequence
- fix any verified text-scaling or contrast failures

The app should not add live regions, forced focus, custom actions, or extra spoken feedback merely to appear more accessible. Each addition should solve a confirmed usability problem without making normal accessibility navigation noisier.

## Android guidance

This policy follows Android's general guidance to use sufficiently large controls, label meaningful interactive elements, exclude decorative elements, use built-in widget accessibility behavior, keep all actions reachable, and test with accessibility tools:

- [Make apps more accessible with Views](https://developer.android.com/guide/topics/ui/accessibility/views/apps-views)
- [Principles for improving accessibility with Views](https://developer.android.com/guide/topics/ui/accessibility/views/principles-views)
- [Test your app's accessibility](https://developer.android.com/guide/topics/ui/accessibility/testing)
