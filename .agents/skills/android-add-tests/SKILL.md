---
name: android-add-tests
description: "Add focused Android tests after a feature implementation is complete. Use when asked to add tests for newly introduced Kotlin Android behavior without changing production code or documentation."
---

# Android Add Tests

The feature implementation is already complete.

Only add tests for the newly introduced Android behavior.

Requirements:

- Do not modify production code unless a minimal testability refactor is absolutely necessary.
- Reuse existing test patterns and utilities.
- Do not increase test coverage outside the scope of this feature.
- Ignore pre-existing gaps unrelated to this change.
- Prefer unit tests over instrumentation tests.
- Add instrumentation tests only if the behavior cannot be verified otherwise.
- Do not update documentation.
- Run only the tests you add or modify.

Return:

1. Behaviors covered.
2. New or modified test files.
3. Any remaining high-risk scenarios that require manual QA.
