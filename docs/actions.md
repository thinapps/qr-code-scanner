# Actions

QR Code Scanner keeps result actions simple and local. The app does not send scanned content to a server. Accepted scan results can be saved only in the app's local scan history. See [Scan History](history.md) for that behavior.

The app intentionally uses one generic result flow instead of building separate action flows for every QR or barcode content type.

## Copy

`Copy` copies the scanned value exactly as it was read from the QR code or barcode.

The app does not show its own copied toast after this action. It relies on Android's native clipboard confirmation, so supported Android versions show only the modern system copy feedback instead of duplicate app and system messages.

## Share

`Share` shares the scanned value exactly as it was read from the QR code or barcode.

The app uses Android's native share sheet with a plain text share intent. It does not pass a custom chooser title because modern Android share sheets may not display app-provided titles in a consistent or obvious place. The system is allowed to show the scanned value, Copy, Quick Share, recent contacts, and app targets using the device's native share UI.

## Open

`Open` is only enabled when the scanned value passes the app's local web-link checks.

The app accepts HTTP and HTTPS links when they have a parsed host and do not include URI user-info. It also accepts likely web domains without a scheme, such as `example.com` or `www.example.com`, and adds `https://` before opening them.

Before parsing, the app rejects embedded whitespace, raw backslashes, ISO control characters, and Unicode bidirectional-control characters. These characters can make a scanned value malformed, create parser differences, or make parts of a displayed link harder to interpret.

The app does not maintain a hardcoded list of valid top-level domains. That would be brittle because valid public suffixes change and there are many possible TLDs.

For schemeless domains, the app checks the host shape so new or uncommon TLDs can still work without an app update:

- at least one dot in the host
- valid dot-separated host labels
- no empty host labels
- no host label longer than 63 characters
- no host label starting or ending with a dash
- only letters, digits, and dashes in host labels
- final label has at least two letters or uses a punycode-style `xn--` prefix

The app also rejects web links that include URI user-info because those links can visually hide the real destination host.

Values with unsupported explicit schemes, missing hosts, schemeless `@` characters, or any of the rejected characters above are not treated as openable web links.

These are minimal local parsing safeguards. The app does not detect phishing, consult an online reputation service, or verify that a destination is trustworthy.

## No destination scoring or domain preview

The app will not add an `Opens: example.com` summary, registrable-domain guess, public-suffix or TLD classification, punycode risk score, or Safe/Suspicious label. Modern domains can use country-code hierarchies, internationalized names, new or specialized TLDs, and legitimate long subdomains, so a small local parser could easily identify the wrong meaningful domain or flag valid links.

An incorrect warning would create noise, while an unflagged link could create false confidence. The app therefore shows the exact scanned value and limits URL handling to basic structural checks used only to decide whether `Open` may be enabled. Passing those checks is not a safety verdict.

## No online lookup or remote analysis

The app will not add a Web Search, Research, Check Link, product lookup, webpage preview, redirect resolver, reputation check, remote decoder, server-side OCR, cloud analysis, or AI-analysis action. It does not fetch webpages in the background or upload scanned values, camera frames, or selected images for remote processing.

Local validation only decides whether `Open` may be enabled. Tapping `Open` explicitly hands the link to another installed app through Android; the scanner itself does not visit, inspect, summarize, or research the destination. Tapping `Share` is also an explicit user action handled by Android's normal share flow rather than automatic app communication.

## No automatic opening

The app will not automatically open a scanned link or launch another app as soon as a code is detected. Every result is shown in the result card first, and the user must explicitly tap `Open` after the value passes the local web-link checks.

Auto-open would make scanning less predictable and could send users to an unexpected destination before they have reviewed the scanned value. The app will not add an auto-open preference or timed redirect.

## Scan feedback

Accepted scans use haptic feedback. The app will not add a scan beep, success sound, spoken confirmation, or sound setting.

A beep can be disruptive in public or quiet places and would require another preference and audio-behavior edge cases. The existing haptic and visible result are enough for this app.

## History preview actions

Tapping a saved history item returns that value to the main scanner screen. From there, the normal Copy, Open, and Share buttons use the same rules as a newly scanned result.

Previewing a saved history item does not create a new history entry or move that item to the top of the history list.

## Type-specific actions

The app will not add dedicated scanned-result actions for Wi-Fi, contacts, email, phone, SMS, calendar events, map locations, or similar content types.

Those actions sound useful, but each one becomes its own parsing and intent-handling feature. Wi-Fi codes, contact cards, calendar events, map links, phone numbers, SMS links, and email links all have format quirks, partial data cases, Android-version differences, and failure modes.

Adding separate buttons for those types would also make the result panel more complicated and easier to get wrong. A broken type-specific action would make the app feel less reliable even when scanning, copying, sharing, and supported web opening still work correctly.

The app keeps the generic result flow instead: preview the raw scanned value, copy it, share it, open it only when it passes the local web-link checks, and save it locally in history.

## Result labels

The app will not add content-type labels such as Website, Text, Email, Phone, Product, Wi-Fi, Contact, Calendar, Location, or similar labels.

Those labels can easily become another parsing feature. Incorrect or inconsistent labels would be just as likely to hurt trust as help, especially for malformed QR payloads, partial contact cards, unusual URL formats, Wi-Fi strings, calendar data, map links, or barcode values that do not clearly map to one type.

The only type-aware action remains local web-link opening, which is already represented by whether the `Open` button is enabled. Everything else stays visible as the raw scanned value so the result panel remains simple and predictable.