<!-- Generated from .agents SSOT. Do not edit manually; run: py -3 .agents/scripts/sync_agents.py -->
---
name: android-repo-review
description: "Review this Kotlin Android multi-module repository for security, stability, Android best practices, maintainability, and testing gaps. Use when asked for a repository or code review."
---

# Android Repo Review

Act as a senior Kotlin Android developer.

Perform a code review of this repository. Focus on:

- Security vulnerabilities: authentication, authorization, secrets, insecure storage, network security, input validation, exported components.
- Stability issues: crashes, lifecycle problems, concurrency bugs, resource leaks.
- Android best practices: Kotlin Flows, Compose, MVVM, Clean Architecture, dependency injection.
- Maintainability and code quality.
- Testing gaps.

Categorize findings by severity:

- Critical
- High
- Medium
- Low

For each finding provide:

- Affected files.
- Explanation.
- Concrete remediation steps.
- Example code fixes when useful.
