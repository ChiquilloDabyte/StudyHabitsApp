# 🎯 Agent Manifest – Personal Task Manager (Java)

## 1. Purpose in one sentence
Build a single-jar desktop app (Java 21 + JavaFX 21) that lets a user **CRUD personal tasks with speed, keyboard-first UX, and WCAG-AA contrast**. No Google Calendar yet.

## 2. Non-negotiables
- Startup ≤ 1.5 s on an M1 Mac / 8 GB RAM.  
- 60 FPS scroll & hover animations (JavaFX Pulse logger ≤ 16 ms).  
- Every screen must pass axe-core contrast ratio ≥ 4.5:1.  
- Zero external DB; state = encrypted JSON file beside jar (`~/.tasker/vault.json`).  

## 3. Visual Design Tokens
| Token | Value |
|-------|-------|
| Background | `#111827` (gray-900) |
| Surface | `#1f2937` (gray-800) |
| Primary | `#38bdf8` (sky-400) |
| Accent | `#f59e0b` (amber-400) |
| Text-primary | `#f9fafb` (gray-50) |
| Text-secondary | `#9ca3af` (gray-400) |
| Error | `#f87171` (red-400) |

## 4. Agent Workflow
1. Parse this manifest → `agent-spec.md`.  
2. Create / update tasks with `agent-task-template.md`.  
3. When a task is done, append **Retro Notes** (bugs, timings, lessons).  
4. Never push to main; always open a PR prefixed `[bot]`.

## 5. Debug Telemetry
- Enable `-XX:+FlightRecorder` in dev run config.  
- Log to `logs/agent-%d.log` (10 MB rolling).  
- Use `javafx.animation.PulseLogger` env var `com.sun.javafx.pulseLogger=true` to print frame budget.