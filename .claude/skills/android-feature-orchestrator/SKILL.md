<!-- Generated from .agents SSOT. Do not edit manually; run: py -3 .agents/scripts/sync_agents.py -->
---
name: android-feature-orchestrator
description: "Orchestrate Android feature workflow: design, implement, test, docs, and optional session squash in strict order with model assignments. Use when running /android-feature-orchestrator or when asked to coordinate a full Android feature delivery."
disable-model-invocation: true
---

# android-feature-orchestrator

Orchestrate the Android feature workflow in strict order. Keep the user in
control at every decision point, and do not perform later stages early.

## Model assignments

- Design and plan revision: GPT-5.6 Sol High.
- Feature implementation: GPT-5.6 Terra Medium.
- Tests and documentation: GPT-5.6 Luna Medium.
- Commit squash: GPT-5.6 Luna Medium.

Use the exact requested model variant when it is available. If a requested
variant is unavailable, stop before that stage and tell the user which model is
missing. Do not silently substitute another model.

## Flags

- `--yolo`: skip intermediate confirmations for design, implementation,
  commits, and the tests prompt. See `--yolo` mode below.
- `--squash`: after every other stage finishes, invoke
  `android-squash-commits` as the final step. This flag never inherits
  `--yolo` approval; always wait for explicit user confirmation before
  rewriting history.

## `--yolo` mode

When invoked with `--yolo`, treat the feature request as approval to run the
entire workflow without intermediate confirmation. Do not ask for plan
approval, plan adjustments, implementation-stage approval, commit approval, or
whether to add tests. Add focused tests by default, then update documentation.

`--squash` is the only exception: even with `--yolo`, never squash until the
user explicitly approves the commit list and final one-liner.

In `--yolo` mode, use the default single-commit implementation flow unless the
request or plan explicitly requires separate commits. Continue through the
remaining applicable stages after a validation failure, but do not create a
commit when the implementation cannot be validated. Report every failure,
skipped action, and manual follow-up only in the final completion report.

## Workflow

### 1. Design

1. Invoke `android-design-feature` with GPT-5.6 Sol High.
2. Require a concise, staged implementation plan. The design stage must not
   modify files.
3. Unless `--yolo` is present, present the plan to the user and ask for
   confirmation.
4. If the user requests changes, send the feedback to GPT-5.6 Sol High and ask
   it to revise the plan. Repeat this review loop until the user confirms the
   plan.

Do not start implementation before explicit plan confirmation unless `--yolo`
is present.

### 2. Implementation

After the plan is confirmed, implement all planned stages with GPT-5.6 Terra
Medium and create one commit by default. Do not ask whether stages should be
squashed.

If the confirmed plan or the user's request calls for separate stages or
commits, implement one stage at a time instead. Before each separate commit,
show the intended scope and ask for confirmation unless `--yolo` is present.
Do not begin the next stage until the user confirms, unless `--yolo` is
present.

For the default single-commit flow, show the complete diff and ask for
confirmation before creating the commit, unless `--yolo` is present.

Use `android-implement-feature` for implementation. It must:

- modify only the minimum files required by the confirmed plan;
- run only affected tests and the necessary compile tasks;
- avoid writing tests or updating documentation;
- stop and report modified files, changes, and recommended later tests when
  implementation is complete.

Never create a commit without the confirmation required by the selected mode.
Never continue if implementation validation fails unless `--yolo` is present.
Outside `--yolo` mode, report the failure and ask the user whether to revise
the plan or fix the current stage.

Never mention yourself in the commit message. Commit message should be concise.

### 3. Tests

When implementation is complete, unless `--yolo` is present, ask:

> Do you want tests added for this feature?

If the user says yes, or `--yolo` is present, invoke `android-add-tests` with
GPT-5.6 Luna Medium.
Keep tests limited to the newly introduced Android behavior and run only the
tests added or modified. Wait for the test stage to finish before continuing.

If the user says no, record that tests were skipped and continue.

### 4. Documentation

After the test decision and any requested test work, invoke
`android-update-docs` with GPT-5.6 Luna Medium. Update only existing
documentation affected by the confirmed feature. Do not create unrelated
documentation or broaden the scope.

### 5. Squash session commits (`--squash` only)

Run this stage only when `--squash` is present, and only after design,
implementation, tests, and documentation are finished. It must be the very
last workflow step.

1. Invoke `android-squash-commits` with GPT-5.6 Luna Medium.
2. Always present the session commits and the proposed one-liner, then wait
   for explicit user approval before rewriting history.
3. Do not treat `--yolo` as approval for this stage.
4. After a successful squash, or after the user declines, stop. Do not start
   any further stages.

## Completion report

Return:

1. The confirmed plan and whether implementation was squashed or separated.
2. Modified production and test files.
3. Validation commands and their results.
4. Documentation sections updated.
5. Whether `--squash` ran, the final commit message if applicable, and whether
   the user approved or declined.
6. Any skipped work, unresolved failures, or manual QA still required.
