# 🔍 First-Scan Improvements

## A. Project Hygiene
| Issue | Severity | Fix |
|-------|----------|-----|
| `gradlew` missing executable bit | Medium | `git update-index --chmod=+x gradlew` |
| No `.gitignore` for `*.log` & `hs_err_pid*` | Low | Add lines |
| `README.md` still Spring-Boot template | High | Replace with JavaFX run instructions |

## B. Performance
- `JsonVaultRepository` loads entire file into memory even for single-task read → introduce streaming `JsonReader`.  
- `TaskListCell` recreates `DateTimeFormatter` per cell → hoist to static final.  
- CSS uses universal selector `*` with `drop-shadow` → scope to `.task-card:hover`.

## C. Visual & UX
- Color palette already defined but not yet bound to CSS variables.  
- Focus ring is native blue → clashes with sky-400; override via `-fx-focus-color`.  
- No empty-state illustration when 0 tasks; add placeholder SVG with amber accent.

## D. Testing
- `TaskControllerTest` uses `Thread.sleep(1000)` for async assertions → migrate to `WaitForAsyncUtils`.  
- Add `Monocle` headless test config for CI (GitHub Actions).

## E. Tooling
- Add Spotless + Google Java Format pre-commit hook.  
- Integrate axe-core via `npm i -D axe-cli` and run against packaged JAR with TestFX.