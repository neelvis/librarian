# Generated from .agents SSOT. Do not edit manually; run: py -3 .agents/scripts/sync_agents.py

# Codex Project Setup

`.agents` is the SSOT for skills, shared instructions, MCP semantics, and agent
role definitions. Codex consumes skills **directly** from `.agents/skills`.

## Mapping

| SSOT | Codex / shared output |
| --- | --- |
| `.agents/skills/` | Direct consume (no `.codex/skills` mirror) |
| `.agents/AGENTS.md` | Generated root `AGENTS.md` |
| `.agents/config/mcp.json` + `runtime.json` | Generated `.codex/config.toml` |
| `.agents/agents/*.toml` | Generated `.codex/agents/*.toml` |
| `.agents/evals/` | Canonical evals (not vendor-copied) |

## Notes

- Do not edit generated Codex files by hand. Change `.agents` and run
  `py -3 .agents/scripts/sync_agents.py --codex`.
- Keep secrets out of MCP configs; use environment variables.
- Invoke workflows by skill name, for example: `Use android-add-tests`.
