# 🧪 Task – {{TASK_TITLE}}

> **Version**: 1.0.0  
> **Created**: {{DATE}}  
> **Branch**: `bot/{{TASK_SLUG}}`

## 1. Objective
{{ONE_LINE_GOAL}}

## 2. Context Snapshot
- Read: `agent-manifest.md` (north-star)  
- Skim: `agent-spec.md` (current hotspots)  
- Browse: `/src/main/java/com/tasker/ui` (JavaFX controllers)  

## 3. Acceptance Criteria
- [ ] UI meets WCAG-AA contrast (axe-core CLI).  
- [ ] No regression in startup time (`./gradlew jfrStart` ≤ 1.5 s).  
- [ ] Unit test ≥ 80 % diff coverage.  
- [ ] JFR shows ≤ 16 ms pulse time for new interaction.

## 4. Debug Checklist
- [ ] Run with `-Dprism.verbose=true` to catch overdraw.  
- [ ] Add `StopWatch` around any new I/O in `JsonVaultRepository`.  
- [ ] If animation > 16 ms, replace with `Timeline` instead of CSS `transition`.

## 5. Retro Notes
_(Agent fills in after PR)_
- Bug: …  
- Fix: …  
- Timing impact: +0.1 s / -0.3 s / none.