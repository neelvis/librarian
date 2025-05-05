#!/usr/bin/env python3
from __future__ import annotations

import importlib.util
import json
import sys
import tempfile
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[3]
SCRIPT = ROOT / ".agents" / "scripts" / "sync_agents.py"


def load_sync_module():
    spec = importlib.util.spec_from_file_location("sync_agents", SCRIPT)
    assert spec and spec.loader
    module = importlib.util.module_from_spec(spec)
    sys.modules["sync_agents"] = module
    spec.loader.exec_module(module)
    return module


class SyncAgentsTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls) -> None:
        cls.sync = load_sync_module()

    def test_parse_requires_flag(self) -> None:
        with self.assertRaises(SystemExit):
            self.sync.parse_args([])

    def test_all_exclusive(self) -> None:
        with self.assertRaises(SystemExit):
            self.sync.parse_args(["--all", "--cursor"])

    def test_selected_vendors(self) -> None:
        args = self.sync.parse_args(["--codex", "--claude"])
        self.assertEqual(self.sync.selected_vendors(args), ["codex", "claude"])

    def test_manifest_direct_skills_for_cursor_and_codex(self) -> None:
        manifest = self.sync.load_manifest()
        self.assertEqual(manifest["vendors"]["cursor"]["skills"], "direct")
        self.assertEqual(manifest["vendors"]["codex"]["skills"], "direct")
        self.assertEqual(manifest["vendors"]["claude"]["skills"], "generate")

    def test_skip_skills_logs_direct(self) -> None:
        manifest = self.sync.load_manifest()
        from io import StringIO
        from contextlib import redirect_stdout

        buf = StringIO()
        with redirect_stdout(buf):
            self.sync.skip_skills("cursor", manifest["vendors"]["cursor"])
        self.assertIn("direct-consume", buf.getvalue())
        self.assertIn(".agents/skills", buf.getvalue())

    def test_remove_obsolete_paths(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            tmp_path = Path(tmp)
            for rel in (
                ".cursor/commands",
                ".cursor/rules",
                ".cursor/skills",
                ".codex/skills",
                ".codex/evals",
            ):
                path = tmp_path / rel
                path.mkdir(parents=True)
                (path / "stale.txt").write_text("old\n", encoding="utf-8")

            original_root = self.sync.ROOT
            try:
                self.sync.ROOT = tmp_path
                self.sync.remove_obsolete_paths(
                    {
                        "obsolete_paths": [
                            ".cursor/commands",
                            ".cursor/rules",
                            ".cursor/skills",
                            ".codex/skills",
                            ".codex/evals",
                        ]
                    }
                )
                for rel in (
                    ".cursor/commands",
                    ".cursor/rules",
                    ".cursor/skills",
                    ".codex/skills",
                    ".codex/evals",
                ):
                    self.assertFalse((tmp_path / rel).exists())
            finally:
                self.sync.ROOT = original_root

    def test_cursor_manifest_skips_rules(self) -> None:
        manifest = self.sync.load_manifest()
        self.assertNotIn("rules", manifest["vendors"]["cursor"]["generate"])
        self.assertIn("mcp", manifest["vendors"]["cursor"]["generate"])

    def test_idempotent_root_instructions(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            tmp_path = Path(tmp)
            agents = tmp_path / ".agents"
            agents.mkdir()
            (agents / "AGENTS.md").write_text("# Test Agents\n", encoding="utf-8")
            manifest = {
                "generated_marker": "Generated from .agents SSOT. Do not edit manually; run: py -3 .agents/scripts/sync_agents.py"
            }
            original_root = self.sync.ROOT
            original_agents = self.sync.AGENTS
            try:
                self.sync.ROOT = tmp_path
                self.sync.AGENTS = agents
                self.sync.render_root_instructions(manifest)
                first = (tmp_path / "AGENTS.md").read_text(encoding="utf-8")
                self.sync.render_root_instructions(manifest)
                second = (tmp_path / "AGENTS.md").read_text(encoding="utf-8")
                self.assertEqual(first, second)
                self.assertIn(manifest["generated_marker"], first)
                self.assertTrue((tmp_path / "CLAUDE.md").exists())
            finally:
                self.sync.ROOT = original_root
                self.sync.AGENTS = original_agents


if __name__ == "__main__":
    unittest.main()
