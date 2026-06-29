# Actions

QR Code Scanner keeps result actions simple and local. The app does not store scan history or send scanned content to a server.

## Copy

`Copy` copies the scanned value exactly as it was read from the QR code.

## Share

`Share` shares the scanned value exactly as it was read from the QR code.

## Open

`Open` is only enabled when the scanned value looks like a web link.

The app accepts normal `http://` and `https://` links. It also accepts likely web domains without a scheme, such as `example.com` or `www.example.com`, and opens them as `https://` links.

The app does not maintain a hardcoded list of valid top-level domains. Instead, it checks the host shape so new or uncommon TLDs can still work without an app update.

Current host checks include:

- at least one dot in the host
- valid dot-separated host labels
- no empty host labels
- no host label longer than 63 characters
- no host label starting or ending with a dash
- only letters, digits, and dashes in host labels
- final label has at least two letters or uses a punycode-style `xn--` prefix

Values with spaces, email-style `@` characters, or unsupported schemes are not treated as openable web links.
