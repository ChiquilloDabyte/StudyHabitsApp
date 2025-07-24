# 🧩 Agent Spec – Current Snapshot

| Key | Value |
|-----|-------|
| Version | 0.2.0 |
| Last Update | 2025-07-24 |
| Entrypoint | `com.tasker.MainApp#main` |
| UI Framework | JavaFX 21 |
| Datastore | `JsonVaultRepository` (single file) |
| Test Coverage | 62 % (unit) / 0 % (e2e) |
| Largest Hotspot (JFR) | `TaskListCell.updateItem` 37 ms spikes on >300 tasks |
| Known Contrast Failure | Delete button on hover (`red-400` on `surface`) → ratio 3.2:1 |

## Next Hot Paths
1. `JsonVaultRepository.saveAll` performs full-file rewrite → O(n²) on bulk edits.  
2. `FilteredList` fires 3× more change events than required when toggling filters.  
3. CSS `drop-shadow` on `.task-card` causes 0.8 ms GPU time per card at 200+ tasks.

## Visual Debt Register
- No dark-mode icon toggles.  
- Scroll-bar thumb almost invisible on `#1f2937`.  
- No keyboard shortcut legend for power users.