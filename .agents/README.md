# Agent configuration SSOT

`.agents` is the single source of truth for project agent skills, shared
instructions, MCP semantics, role definitions, and eval prompts.

Do not treat `.cursor`, `.codex`, or `.claude` trees as authoritative for
skills. Edit files here, then run the converter.

## Direct consume vs generated adapter

| Vendor | Skills / instructions | Still generated |
| --- | --- | --- |
| **Cursor** | Skills from `.agents/skills`; instructions from root `AGENTS.md` | `.cursor/mcp.json` only (Cursor MCP path) |
| **Codex** | Skills from `.agents/skills`; instructions from root `AGENTS.md` | `.codex/config.toml`, `.codex/agents/*.toml`, `.codex/README.md` |
| **Claude Code** | Skills mirrored to `.claude/skills` | `.claude/skills/*`, root `.mcp.json`, `CLAUDE.md` |

Not generated / removed on sync: `.cursor/commands`, `.cursor/rules`,
`.cursor/skills`, `.codex/skills`, `.codex/evals`.

Shared instructions live in `.agents/AGENTS.md` and are generated to root
`AGENTS.md` and `CLAUDE.md`.

## Layout

```text
.agents/
  AGENTS.md                 # Shared project instructions
  manifest.json             # Vendor modes + output paths
  config/mcp.json           # Semantic MCP definitions
  config/runtime.json       # Codex-style agent runtime limits/features
  agents/*.toml             # Multi-agent role definitions
  skills/<name>/SKILL.md    # Canonical skills (Cursor + Codex read here)
  evals/                    # Regression prompts
  scripts/sync_agents.py    # Converter
```

## Converter

```powershell
py -3 .agents/scripts/sync_agents.py --all
py -3 .agents/scripts/sync_agents.py --cursor
py -3 .agents/scripts/sync_agents.py --codex
py -3 .agents/scripts/sync_agents.py --claude
```

- `--all` cannot be combined with vendor flags.
- Direct-consume skill targets log a skip message and do not create a mirrored
  skill tree.
- Generated files include a provenance marker. Prefer changing `.agents` and
  re-running sync instead of editing adapters by hand.

## Updating a skill

Use the `update-skill` skill, or:

1. Edit `.agents/skills/<name>/`.
2. Run `py -3 .agents/scripts/sync_agents.py --all`.
3. Run `py -3 -m unittest discover -s .agents/scripts/tests -v`.
