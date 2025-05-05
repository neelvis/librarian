---
name: update-skill
description: "Update a canonical skill under .agents/skills and propagate vendor adapters via sync_agents.py. Use when editing, adding, or fixing an agent skill and vendor folders must stay in sync."
---

# Update Skill

`.agents` is the single source of truth for agent skills and related settings.
Never edit generated vendor copies as the primary change.

## Instructions

1. Confirm the target skill name (folder under `.agents/skills/<name>/`).
2. If the skill does not exist, create `.agents/skills/<name>/SKILL.md` with
   required YAML frontmatter (`name`, `description`) and the skill body.
3. Apply the requested updates only to files under `.agents/skills/<name>/`
   (and optionally `.agents/manifest.json` if exposure/metadata changes).
4. Do not hand-edit generated outputs under `.cursor/`, `.codex/`, `.claude/`,
   root `AGENTS.md`, `CLAUDE.md`, or `.mcp.json` except by running the sync
   script.
5. Propagate with the converter:

```powershell
py -3 .agents/scripts/sync_agents.py --all
```

Use a narrower flag when only one vendor adapter is required:

```powershell
py -3 .agents/scripts/sync_agents.py --cursor
py -3 .agents/scripts/sync_agents.py --codex
py -3 .agents/scripts/sync_agents.py --claude
```

6. Run focused sync tests:

```powershell
py -3 -m unittest discover -s .agents/scripts/tests -v
```

7. Confirm a second sync produces no diff for generated adapters.
8. Report: skill path changed, sync flags used, vendors skipped as
   direct-consume, and any manual follow-up.

## Notes

- Cursor and Codex load skills directly from `.agents/skills` — sync will log
  and skip skill-tree generation for those vendors.
- Cursor project instructions come from root `AGENTS.md`; do not recreate
  `.cursor/rules` or `.cursor/commands`.
- Claude skill adapters under `.claude/skills` are generated when `--claude`
  or `--all` is used.
- Sync also deletes obsolete mirrors listed in `manifest.json`
  (`obsolete_paths`).
