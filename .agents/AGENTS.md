---
description: Android agent rules.
alwaysApply: true
---

# Development Workflow

## General Rules

- Read task-related documentation before modifying code.
- Keep changes tightly scoped and do not modify generated files manually.
- Agent skills and settings SSOT is `.agents/`. Edit there and run
  `py -3 .agents/scripts/sync_agents.py --all` (see `.agents/README.md`).
  Do not treat `.cursor/`, `.codex/`, or `.claude/` skill mirrors as authoritative.
- Never expose secrets, credentials, private keys, tokens, or proprietary data.
- Run relevant formatting, static checks, and tests before declaring completion.

## Multi-Agent Workflow

For tasks involving multiple modules, architectural analysis, substantial
refactoring, security review, or uncertain failure causes:

1. Keep the root agent as coordinator.
2. Delegate independent, bounded discovery work to subagents and wait for their results before finalizing a plan.
3. Use `explorer` for read-only discovery and `reviewer` for risk analysis.
4. Assign write work only after file ownership is clear; avoid concurrent edits to the same files.
5. Prefer one `implementer` for tightly coupled changes. Parallel writers require disjoint files and settled interfaces.
6. After implementation, use independent `tester` and `reviewer` agents when the task warrants validation beyond a focused local check.
7. Reconcile findings and verify that the final diff matches the requested scope.

Do not delegate routine, clearly bounded changes when coordination cost exceeds the benefit. Prefer parallel agents for read-heavy exploration, review, test analysis, and log triage.

## Required Phases For Substantial Work

### Discovery

Return concise summaries with file references for architecture, dependency,
security, test/regression, performance, or concurrency analysis as applicable.

### Planning

The coordinator must define intended behavior, expected files, implementation
sequence, acceptance criteria, validation commands, and material risks.

### Completion

Do not report success until required checks pass and material review findings
are resolved or explicitly documented.

## Project Nature

This project is **Librarian** (`Booksh`): a native Android multi-module app for
managing a home library (add books, search, dashboard, notifications,
onboarding, profile).

When proposing architecture, features, refactoring, or tests:

- Keep Clean Architecture layering (`core:domain`, `core:data`, `core:ui`,
  `core:database`, `core:model`, `feature:*`, `app`, `common`).
- Put business logic in domain use cases; keep UI thin.
- Prefer self-documenting code.
- Do not store secrets in source; keep keystore and credential files out of
  commits.

# Tech Stack

- Kotlin
- Jetpack Compose
- Hilt
- Coroutines / Flow

# Architecture

- Clean Architecture
- MVVM
- Multi-module
- Domain layer must not depend on Android SDK.
- Use cases contain app business logic.
- Repositories return `Result<T>` where the existing module conventions already use that pattern.

# Android Platform

- Avoid deprecated Android APIs.
- Use the Activity Result API for result-based Android interactions.
- Target the latest stable SDK supported by the project.

# Compose

- Use Material 3.
- State flows down, events flow up.
- Prefer stateless content composables.

# External Documentation

Use Context7 when:
- Generating Android SDK code
- Generating Jetpack Compose code
- Using AndroidX libraries
- Using Retrofit
- Using Room
- Using Hilt
- Using Ktor
- Writing Gradle configuration

Prefer official documentation from Context7 over model memory when the Context7 MCP server is available.

# Before implementing features:

Locate relevant modules from `settings.gradle.kts` and existing package layout:
- App shell: `app/`
- Shared: `common/`
- Core: `core/{domain,data,ui,database,model}/`
- Features: `feature/{dashboard,profile,add-books,search,notification,onboarding}/`

Only read additional documentation directly related to the task.
Avoid loading unrelated modules.

# When implementing features:

1. Follow project rules
2. Prefer readable and maintainable code.
3. Explain architectural trade-offs.
4. Avoid unnecessary abstractions.
5. Follow existing project conventions.
6. Suggest tests for new functionality.
7. Don't use wildcard imports
8. Apply ktlint rules when writing code

# When implementing large features:

1. Produce a plan.
2. Wait for approval.
3. Implement one phase only.
4. Commit after each phase.

# Scope Control
- Do not perform repository-wide analysis.
- Only inspect files directly relevant to the task.
- Avoid scanning:
  - test/** unless requested
  - unrelated feature modules
- If more than 15 files appear relevant, ask for confirmation before continuing.
- Run only affected tests.
- Prefer compile tasks over assemble tasks.
