<!-- Generated from .agents SSOT. Do not edit manually; run: py -3 .agents/scripts/sync_agents.py -->
---
name: android-verify-change
description: "Validate a scoped Kotlin Android change with the narrowest relevant compile, test, static, and manual checks. Use after implementing or reviewing an Android change, especially before declaring it complete."
---

# Android Verify Change

Validate only the changed Android behavior; do not widen the task into a repository-wide test run.

1. Read applicable `AGENTS.md` instructions and inspect the diff to identify affected modules and behavior.
2. Select the narrowest relevant Gradle checks. Prefer affected-module compile and unit-test tasks over `assemble` or broad test suites.
3. Run configured static checks or formatting only when they apply to the changed files.
4. For UI changes, verify the affected state in light and dark themes when the project supports them; explicitly identify manual checks that could not be run.
5. For permissions, notifications, navigation, Room, or Firebase-related changes, validate the relevant Android entry point.
6. Do not change production code. Add or modify tests only when explicitly requested.

Return changed behavior and acceptance criteria; commands and pass/fail results; manual checks completed, skipped, or blocked; failures with essential evidence and reproduction steps; and remaining risk with confidence.
