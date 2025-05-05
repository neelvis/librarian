---
name: android-webview-security-review
description: "Review Android app changes for security risks. Use for authentication, storage, network-security, permission, notification, deep-link, Firebase token, Room, or exported-component changes."
---

# Android Security Review

Perform a read-only review of the Android trust boundary. Do not edit files.

1. Read applicable `AGENTS.md` instructions, inspect the changed Android path,
   and limit scope to affected modules.
2. Review relevant controls: secrets and keystore handling, encrypted/local
   storage, network and cleartext configuration, runtime permissions,
   notification handling, deep links / intent filters, exported components,
   Firebase token handling, and Room data exposure.
3. Use Context7 for Android/AndroidX API behavior when available. Distinguish
   confirmed defects from hardening suggestions.
4. Do not expose secrets or use production credentials.

Report findings only, ordered by severity. Include the affected file and exact
reference, exploit or failure scenario, concrete remediation, validation or
test gap, and an explicit statement when no material findings exist.
