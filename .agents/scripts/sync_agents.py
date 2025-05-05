#!/usr/bin/env python3
"""Convert .agents SSOT into vendor-specific adapter files.

Vendors that natively consume .agents skills are skipped for skill-tree
generation (logged as direct-consume). Only formats that cannot point at
.agents receive generated adapters.
"""

from __future__ import annotations

import argparse
import json
import re
import shutil
import sys
from pathlib import Path
from typing import Iterable

ROOT = Path(__file__).resolve().parents[2]
AGENTS = ROOT / ".agents"
MANIFEST_PATH = AGENTS / "manifest.json"

SECRET_PATTERNS = (
    re.compile(r"(?i)api[_-]?key\s*[:=]\s*['\"]?[A-Za-z0-9_\-]{16,}"),
    re.compile(r"(?i)secret\s*[:=]\s*['\"]?[A-Za-z0-9_\-]{8,}"),
    re.compile(r"AIza[0-9A-Za-z\-_]{20,}"),
)


def load_manifest() -> dict:
    return json.loads(MANIFEST_PATH.read_text(encoding="utf-8"))


def marker_line(manifest: dict, prefix: str) -> str:
    return f"{prefix} {manifest['generated_marker']}"


def is_generated(path: Path, manifest: dict) -> bool:
    if not path.is_file():
        return False
    try:
        text = path.read_text(encoding="utf-8")
    except OSError:
        return False
    return manifest["generated_marker"] in text


def write_text(path: Path, content: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    tmp = path.with_suffix(path.suffix + ".tmp")
    tmp.write_text(content, encoding="utf-8", newline="\n")
    tmp.replace(path)


def copy_tree(src: Path, dest: Path) -> None:
    if dest.exists():
        shutil.rmtree(dest)
    shutil.copytree(src, dest)


def validate_no_literal_secrets(path: Path, text: str) -> None:
    for pattern in SECRET_PATTERNS:
        if pattern.search(text):
            raise SystemExit(f"Refusing to write possible secret literal in {path}")


def parse_args(argv: Iterable[str] | None = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Sync .agents SSOT to Cursor, Codex, and Claude adapters."
    )
    parser.add_argument("--all", action="store_true", help="Generate all vendor adapters.")
    parser.add_argument("--cursor", action="store_true", help="Generate Cursor adapters only.")
    parser.add_argument("--codex", action="store_true", help="Generate Codex adapters only.")
    parser.add_argument("--claude", action="store_true", help="Generate Claude adapters only.")
    args = parser.parse_args(list(argv) if argv is not None else None)
    selected = [args.all, args.cursor, args.codex, args.claude]
    if not any(selected):
        parser.error("Specify --all, or one or more of --cursor, --codex, --claude")
    if args.all and any([args.cursor, args.codex, args.claude]):
        parser.error("--all cannot be combined with vendor-specific flags")
    return args


def selected_vendors(args: argparse.Namespace) -> list[str]:
    if args.all:
        return ["cursor", "codex", "claude"]
    vendors: list[str] = []
    if args.cursor:
        vendors.append("cursor")
    if args.codex:
        vendors.append("codex")
    if args.claude:
        vendors.append("claude")
    return vendors


def render_root_instructions(manifest: dict) -> None:
    source = (AGENTS / "AGENTS.md").read_text(encoding="utf-8")
    header = marker_line(manifest, "<!--") + " -->\n"
    for name in ("AGENTS.md", "CLAUDE.md"):
        path = ROOT / name
        content = header + source
        if not source.startswith("---"):
            content = header + source
        validate_no_literal_secrets(path, content)
        write_text(path, content)
        print(f"wrote {path.relative_to(ROOT)}")


def remove_obsolete_paths(manifest: dict) -> None:
    for rel in manifest.get("obsolete_paths", []):
        path = ROOT / rel
        if not path.exists():
            continue
        if path.is_dir():
            shutil.rmtree(path)
        else:
            path.unlink()
        print(f"removed obsolete {rel}")


def render_cursor_mcp(manifest: dict) -> None:
    src = AGENTS / "config" / "mcp.json"
    dest = ROOT / ".cursor" / "mcp.json"
    data = json.loads(src.read_text(encoding="utf-8"))
    # Preserve Cursor JSON shape; attach marker as non-functional sibling key is risky.
    # Write with a leading comment is invalid JSON, so keep clean JSON and log provenance.
    text = json.dumps(data, indent=2) + "\n"
    validate_no_literal_secrets(dest, text)
    write_text(dest, text)
    marker = ROOT / ".cursor" / "GENERATED.md"
    write_text(
        marker,
        marker_line(manifest, "#")
        + "\n\nCursor MCP config is generated from `.agents/config/mcp.json`.\n"
        + "Skills are consumed directly from `.agents/skills`.\n"
        + "Project instructions come from root `AGENTS.md` (no `.cursor/rules` mirror).\n",
    )
    print(f"wrote {dest.relative_to(ROOT)}")
    print(f"wrote {marker.relative_to(ROOT)}")


def render_codex_config(manifest: dict) -> None:
    runtime = json.loads((AGENTS / "config" / "runtime.json").read_text(encoding="utf-8"))
    mcp = json.loads((AGENTS / "config" / "mcp.json").read_text(encoding="utf-8"))
    lines = [
        marker_line(manifest, "#"),
        "#",
        "# Project-local Codex MCP and agent runtime configuration.",
        "# Keep secrets out of this file. Set CONTEXT7_API_KEY in the environment.",
        "",
        "[agents]",
        f"max_threads = {runtime['agents']['max_threads']}",
        f"max_depth = {runtime['agents']['max_depth']}",
        f"job_max_runtime_seconds = {runtime['agents']['job_max_runtime_seconds']}",
        f"interrupt_message = {str(runtime['agents']['interrupt_message']).lower()}",
        "",
        "[features]",
        f"goals = {str(runtime['features']['goals']).lower()}",
        f"multi_agent = {str(runtime['features']['multi_agent']).lower()}",
        "",
    ]
    servers = mcp.get("mcpServers", {})
    for name in sorted(servers):
        server = servers[name]
        if "url" in server:
            lines.append(f"[mcp_servers.{name}]")
            lines.append(f'url = "{server["url"]}"')
            env = server.get("env", {})
            if "CONTEXT7_API_KEY" in env:
                lines.append(
                    'env_http_headers = { "CONTEXT7_API_KEY" = "CONTEXT7_API_KEY" }'
                )
            lines.append("")
        elif "command" in server:
            lines.append(f"[mcp_servers.{name}]")
            lines.append(f'command = "{server["command"]}"')
            args = server.get("args", [])
            args_lit = ", ".join(f'"{a}"' for a in args)
            lines.append(f"args = [{args_lit}]")
            lines.append("")
            env = server.get("env", {})
            if env:
                lines.append(f"[mcp_servers.{name}.env]")
                for key in sorted(env):
                    lines.append(f'{key} = "{env[key]}"')
                lines.append("")
    dest = ROOT / ".codex" / "config.toml"
    content = "\n".join(lines).rstrip() + "\n"
    validate_no_literal_secrets(dest, content)
    write_text(dest, content)
    print(f"wrote {dest.relative_to(ROOT)}")


def render_codex_agents(manifest: dict) -> None:
    dest_dir = ROOT / ".codex" / "agents"
    dest_dir.mkdir(parents=True, exist_ok=True)
    for src in sorted((AGENTS / "agents").glob("*.toml")):
        dest = dest_dir / src.name
        body = src.read_text(encoding="utf-8")
        content = marker_line(manifest, "#") + "\n" + body
        if not body.endswith("\n"):
            content += "\n"
        validate_no_literal_secrets(dest, content)
        write_text(dest, content)
        print(f"wrote {dest.relative_to(ROOT)}")


def render_codex_readme(manifest: dict) -> None:
    dest = ROOT / ".codex" / "README.md"
    content = f"""{marker_line(manifest, "#")}

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
"""
    write_text(dest, content)
    print(f"wrote {dest.relative_to(ROOT)}")


def render_claude_skills(manifest: dict) -> None:
    src_root = AGENTS / "skills"
    dest_root = ROOT / ".claude" / "skills"
    if dest_root.exists():
        # Remove previously generated skill trees only.
        for child in list(dest_root.iterdir()):
            marker_candidate = child / "SKILL.md" if child.is_dir() else child
            if child.is_dir() and is_generated(child / "SKILL.md", manifest):
                shutil.rmtree(child)
            elif child.is_dir() and (child / "SKILL.md").exists():
                # Replace unmanaged mirrors during sync of Claude adapters.
                shutil.rmtree(child)
    dest_root.mkdir(parents=True, exist_ok=True)
    for skill_dir in sorted(p for p in src_root.iterdir() if p.is_dir()):
        skill_md = skill_dir / "SKILL.md"
        if not skill_md.exists():
            continue
        dest_dir = dest_root / skill_dir.name
        copy_tree(skill_dir, dest_dir)
        dest_skill = dest_dir / "SKILL.md"
        body = dest_skill.read_text(encoding="utf-8")
        header = marker_line(manifest, "<!--") + " -->\n"
        if manifest["generated_marker"] not in body:
            body = header + body
        validate_no_literal_secrets(dest_skill, body)
        write_text(dest_skill, body)
        print(f"wrote {dest_skill.relative_to(ROOT)}")


def render_claude_mcp(manifest: dict) -> None:
    src = AGENTS / "config" / "mcp.json"
    dest = ROOT / ".mcp.json"
    data = json.loads(src.read_text(encoding="utf-8"))
    text = json.dumps(data, indent=2) + "\n"
    validate_no_literal_secrets(dest, text)
    write_text(dest, text)
    marker = ROOT / ".claude" / "GENERATED.md"
    write_text(
        marker,
        marker_line(manifest, "#")
        + "\n\nClaude MCP project config is generated to root `.mcp.json` "
        + "from `.agents/config/mcp.json`.\n"
        + "Skills under `.claude/skills` are generated adapters.\n",
    )
    print(f"wrote {dest.relative_to(ROOT)}")
    print(f"wrote {marker.relative_to(ROOT)}")


def skip_skills(vendor: str, cfg: dict) -> None:
    mode = cfg.get("skills", "generate")
    path = cfg.get("skills_path", "")
    notes = cfg.get("notes", "")
    if mode == "direct":
        print(
            f"skip {vendor} skills generation: direct-consume "
            f"({path}). {notes}"
        )
    else:
        print(f"{vendor} skills mode={mode} path={path}")


def sync_vendor(vendor: str, manifest: dict) -> None:
    cfg = manifest["vendors"][vendor]
    print(f"== {vendor} ==")
    skip_skills(vendor, cfg)
    generate = set(cfg.get("generate", []))

    if vendor == "cursor":
        if "mcp" in generate:
            render_cursor_mcp(manifest)
        if "root_instructions" in generate:
            render_root_instructions(manifest)
        return

    if vendor == "codex":
        if "config" in generate:
            render_codex_config(manifest)
        if "agents" in generate:
            render_codex_agents(manifest)
        if "readme" in generate:
            render_codex_readme(manifest)
        if "root_instructions" in generate:
            render_root_instructions(manifest)
        return

    if vendor == "claude":
        if "skills" in generate:
            render_claude_skills(manifest)
        if "mcp" in generate:
            render_claude_mcp(manifest)
        if "root_instructions" in generate:
            render_root_instructions(manifest)
        return

    raise SystemExit(f"Unknown vendor: {vendor}")


def main(argv: Iterable[str] | None = None) -> int:
    args = parse_args(argv)
    manifest = load_manifest()
    remove_obsolete_paths(manifest)
    vendors = selected_vendors(args)
    # Root instructions should be written once even if multiple vendors request it.
    wrote_instructions = False
    for vendor in vendors:
        cfg = manifest["vendors"][vendor]
        generate = set(cfg.get("generate", []))
        # Temporarily remove root_instructions to dedupe; restore via flag.
        if "root_instructions" in generate and wrote_instructions:
            cfg = dict(cfg)
            cfg["generate"] = [g for g in cfg.get("generate", []) if g != "root_instructions"]
            manifest_view = dict(manifest)
            manifest_view["vendors"] = dict(manifest["vendors"])
            manifest_view["vendors"][vendor] = cfg
            sync_vendor(vendor, manifest_view)
        else:
            sync_vendor(vendor, manifest)
            if "root_instructions" in generate:
                wrote_instructions = True
    print("sync complete")
    return 0


if __name__ == "__main__":
    sys.exit(main())
