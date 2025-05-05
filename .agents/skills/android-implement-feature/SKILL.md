---
name: android-implement-feature
description: "Implement a previously designed Kotlin Android feature in small confirmed stages. Use when asked to implement from an approved plan without adding tests or documentation."
---

# Android Implement Feature

Implement the designed feature according to the approved plan.

Rules:

- Do not perform repository-wide analysis.
- Do not implement all stages at once unless explicitly requested.
- Make small commits, each containing one stage unless explicitly requested.
- Modify the minimum number of files.
- Run affected unit tests only.
- Run only compile tasks necessary to validate the implementation.
- Ask for confirmation before every commit.
- Ask for confirmation before implementing the next stage.
- Never add commit trailers that credit the agent or Cursor, including
  `Co-authored-by: Cursor <cursoragent@cursor.com>` or any similar
  `Co-authored-by` line.
- Do not write tests.
- Do not update documentation.
- Do not duplicate existing design-system components.
- When creating screens or widgets, provide `@Preview` composables for them.
  Do not add previews for every atomic widget when those widgets can be
  covered by a larger parent preview. Provide a reasonable amount of
  variants (for example light/dark, key states), not an exhaustive matrix.
- Stop after implementation is complete.

Return:

1. Modified files.
2. Summary of changes.
3. Recommended tests that should be added later.
