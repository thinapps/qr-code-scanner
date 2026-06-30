# Actions

QR Code Scanner keeps result actions simple and local. The app does not send scanned content to a server. Accepted scan results can be saved only in the app's local scan history. See [Scan History](history.md) for that behavior.

## Copy

`Copy` copies the scanned value exactly as it was read from the QR code.

## Share

`Share` shares the scanned value exactly as it was read from the QR code.

## Open

`Open` is only enabled when the scanned value looks like a web link.

The app accepts normal `http://` and `https://` links when they have a real host and do not include URI user-info. It also accepts likely web domains without a scheme, such as `example.com` or `www.example.com`, and opens them as `https://` links.

The app does not maintain a hardcoded list of valid top-level domains. That would be brittle because valid public suffixes change and there are many possible TLDs.

For schemeless domains, the app checks the host shape so new or uncommon TLDs can still work without an app update:

- at least one dot in the host
- valid dot-separated host labels
- no empty host labels
- no host label longer than 63 characters
- no host label starting or ending with a dash
- only letters, digits, and dashes in host labels
- final label has at least two letters or uses a punycode-style `xn--` prefix

The app also rejects web links that include URI user-info, such as `https://example.com@other.example`, because those links can visually hide the real destination host.

Values with spaces, schemeless `@` characters, URI user-info, or unsupported schemes are not treated as openable web links.

## History preview actions

Tapping a saved history item returns that value to the main scanner screen. From there, the normal Copy, Open, and Share buttons use the same rules as a newly scanned result.

Previewing a saved history item does not create a new history entry or move that item to the top of the history list.

## Result labels

The app does not currently label scanned results as Website, Text, Email, Phone, or other content types.

For now, labels would mostly repeat the existing action state: values accepted by `Open` would be web links, and everything else would behave like plain text. Adding labels at this stage would add visual clutter without making the app much safer or clearer.

Result labels can be reconsidered later if the app adds dedicated actions for more content types, such as email, phone, SMS, Wi-Fi, contacts, calendar events, or locations.
