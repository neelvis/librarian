---
name: android-debug-wrapper
description: "Diagnose Android app failures using focused reproduction and Android evidence. Use for crashes, blank or broken screens, lifecycle and permission issues, notification failures, Room/data issues, or navigation regressions."
---

# Android Debug Feature

Diagnose the failure with the smallest useful Android evidence set.

1. Read the applicable `AGENTS.md`, then locate the affected module from
   `settings.gradle.kts` and package layout (`app`, `common`, `core/*`,
   `feature/*`).
2. Collect the smallest useful evidence: reproduction steps, device/OS/app
   version, relevant Logcat excerpt, and recent related changes.
3. Classify the failure before proposing a fix: UI/presentation, domain/data,
   platform integration (permissions, notifications, Firebase), or insufficient
   evidence.
4. Inspect only the code path implied by the evidence. Use Context7 for
   Android/AndroidX API behavior when available.
5. Reproduce or narrow the issue with non-destructive checks. Never request or
   expose production credentials or keystore secrets.
6. Implement fixes only when explicitly requested.

Return reproduction status and essential evidence; most likely owner layer;
root-cause hypothesis with file references and confidence; smallest next
diagnostic or fix; and validation performed with remaining unknowns.
