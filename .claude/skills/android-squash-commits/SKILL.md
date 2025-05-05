<!-- Generated from .agents SSOT. Do not edit manually; run: py -3 .agents/scripts/sync_agents.py -->
---
name: android-squash-commits
description: "Squash all commits created in the current session into one short one-liner that summarizes main code changes only. Use when asked to squash session commits, or when android-feature-orchestrator is invoked with --squash."
---

# Android Squash Commits

Squash every commit created in the current agent session into a single commit
with a short one-liner message.

## Scope

- Include only commits introduced during this session on the current branch.
- Do not rewrite history older than this session.
- Do not push unless the user explicitly asks.

## Commit message rules

- Write one short line that summarizes the main production code changes.
- Do not mention tests, documentation, skill/config churn, or follow-up chores
  unless those were the only changes.
- Never include agent credit trailers, including any `Co-authored-by:` line
  (for example `Co-authored-by: Cursor <cursoragent@cursor.com>`).
- Never mention yourself, Cursor, or the agent in the message.

## Approval

Always show the user:

1. The list of session commits that will be squashed.
2. The proposed final one-liner commit message.
3. The squash method you intend to use.

Wait for explicit user approval before rewriting history. Do not proceed on
`--yolo` alone.

## Procedure

1. Identify session commits with `git log` / `git status` relative to the
   branch tip before this session started.
2. If there is only one session commit, still rewrite its message when it
   violates the message rules above; otherwise report that no squash is
   needed.
3. After approval, squash into one commit and apply the approved one-liner.
4. Verify with `git log` and `git status` that:
   - exactly one replacement commit remains for the session work;
   - the message is the approved one-liner;
   - no `Co-authored-by` trailer remains.
5. Stop. Do not push, open a PR, or continue into other stages.

## Return

1. Commits that were squashed.
2. Final commit hash and message.
3. Confirmation that `Co-authored-by` trailers were removed.
4. Any manual follow-up (for example force-push needed if the branch was
   already pushed).
