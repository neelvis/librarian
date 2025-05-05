---
name: android-update-docs
description: "Update existing Android project documentation after feature implementation and tests are complete. Use when asked to update docs only for changed behavior."
---

# Android Update Docs

The feature implementation and tests are already complete.

Only update existing documentation affected by this feature.

Constraints:

- Modify existing documents instead of creating new ones whenever possible.
- Update only sections that became outdated.
- Do not rewrite unaffected sections.
- Do not improve wording outside the changed scope.
- Do not add tutorials, migration guides, or QA checklists unless explicitly requested.
- Do not inspect unrelated modules.

Return:

1. Sections updated.
2. Brief rationale for each change.
3. Any documentation gaps requiring a separate task.
