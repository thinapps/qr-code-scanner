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

`Open` is only enabled when the scanned value looks like a web link.

The app accepts normal web links when they have a real host and do not include URI user-info. It also accepts likely web domains without a scheme, such as `example.com` or `www.example.com`, and opens them as secure web links.

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

Values with spaces, schemeless `@` characters, URI user-info, or unsupported schemes are not treated as openable web links.

## History preview actions

Tapping a saved history item returns that value to the main scanner screen. From there, the normal Copy, Open, and Share buttons use the same rules as a newly scanned result.

Previewing a saved history item does not create a new history entry or move that item to the top of the history list.

## Type-specific actions

The app will not add dedicated scanned-result actions for Wi-Fi, contacts, email, phone, SMS, calendar events, map locations, or similar content types.

Those actions sound useful, but each one becomes its own parsing and intent-handling feature. Wi-Fi codes, contact cards, calendar events, map links, phone numbers, SMS links, and email links all have format quirks, partial data cases, Android-version differences, and failure modes.

Adding separate buttons for those types would also make the result panel more complicated and easier to get wrong. A broken type-specific action would make the app feel less reliable even when scanning, copying, sharing, and safe web opening still work correctly.

The app keeps the generic result flow instead: preview the raw scanned value, copy it, share it, open it only when it looks like a safe web link, and save it locally in history.

## Result labels

The app does not label scanned results as Website, Text, Email, Phone, Product, Wi-Fi, Contact, Calendar, Location, or other content types.

For now, labels would add visual clutter without changing the core behavior. The only type-aware action remains safe web opening, which is already represented by whether the `Open` button is enabled.
