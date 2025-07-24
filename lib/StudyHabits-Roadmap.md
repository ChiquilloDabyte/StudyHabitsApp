# 🗺️ StudyHabits App - Performance & Feature Roadmap

> **Last Updated**: July 23, 2025  
> **Focus**: Performance optimization without sacrificing quality or security

## 🚀 High-Priority Performance Optimizations

### 1. Database Layer Improvements
**Status**: ⏰ In Progress  
**Impact**: High (37ms → ≤16ms task loading)
- [x] **Task Created**: `task-db-optimization.md`
- [ ] Add SQLite indexes for `idUsuario`, `fechaEntrega` columns
- [ ] Implement connection pooling (HikariCP)
- [ ] Cache prepared statements for frequent queries
- [ ] Batch operations for bulk inserts/updates
- [ ] Implement result set streaming for large datasets

### 2. UI Component Optimization  
**Status**: 🔍 Analysis Needed  
**Impact**: High (60 FPS target, reduce GPU overhead)
- [ ] Fix `TaskListCell.updateItem` 37ms spikes on >300 tasks
- [ ] Cache `DateTimeFormatter` instances (currently recreated per cell)
- [ ] Optimize CSS `drop-shadow` on `.task-card` (0.8ms GPU time per card)
- [ ] Implement virtual scrolling for large task lists
- [ ] Replace heavy CSS transitions with JavaFX `Timeline`

### 3. Memory Management
**Status**: 📋 Planned  
**Impact**: Medium (startup time, overall responsiveness)
- [ ] Profile memory usage with JFR on large datasets (1000+ tasks)
- [ ] Implement lazy loading for task details
- [ ] Add object pooling for frequently created UI components
- [ ] Optimize `FilteredList` change events (currently fires 3× more than needed)

## 🎨 User Experience Enhancements

### 4. Accessibility & Visual Polish
**Status**: 📋 Planned  
**Impact**: Medium (WCAG compliance, user satisfaction)
- [ ] Fix contrast failure: Delete button hover (red-400 on surface) → 4.5:1 ratio
- [ ] Implement dark-mode toggle with proper icon indicators
- [ ] Add keyboard shortcut legend for power users
- [ ] Improve scroll-bar visibility on `#1f2937` background
- [ ] Add empty-state illustration with amber accent

### 5. Advanced Features
**Status**: 🔮 Future  
**Impact**: Medium-High (competitive advantage)
- [ ] Smart task scheduling with AI suggestions
- [ ] Pomodoro timer integration
- [ ] Task priority matrix (Eisenhower Matrix)
- [ ] Progress analytics and study habit insights
- [ ] Calendar integration (Google Calendar API already included)
- [ ] Collaborative study groups
- [ ] Export/import functionality (CSV, JSON)

## 🔧 Technical Debt & Infrastructure

### 6. Code Quality & Testing
**Status**: 📋 Planned  
**Impact**: Medium (maintainability, reliability)
- [ ] Increase test coverage from 62% to 80%+ (unit tests)
- [ ] Add end-to-end testing with TestFX
- [ ] Implement headless testing with Monocle for CI
- [ ] Replace `Thread.sleep(1000)` with `WaitForAsyncUtils` in tests
- [ ] Add Spotless + Google Java Format pre-commit hooks

### 7. Build & DevOps Improvements
**Status**: 📋 Planned  
**Impact**: Low-Medium (developer experience)
- [ ] Fix missing executable bit on `gradlew`
- [ ] Add comprehensive `.gitignore` for `*.log` & `hs_err_pid*`
- [ ] Replace Spring-Boot template README with JavaFX instructions
- [ ] Integrate axe-core via `npm i -D axe-cli` for automated accessibility testing
- [ ] Add GitHub Actions CI pipeline

## 🔐 Security & Data Protection

### 8. Enhanced Data Security
**Status**: 🔍 Analysis Needed  
**Impact**: High (user trust, data protection)
- [ ] **Current**: SQLite database with basic storage
- [ ] **Target**: Encrypted JSON vault as per agent manifest
- [ ] Implement AES-256 encryption for task data
- [ ] Add secure password hashing (bcrypt - already included)
- [ ] Implement secure session management
- [ ] Add data backup and recovery mechanisms

### 9. Authentication Improvements
**Status**: 📋 Planned  
**Impact**: Medium (security, user experience)
- [ ] Add two-factor authentication support
- [ ] Implement password strength requirements
- [ ] Add "remember me" functionality with secure tokens
- [ ] Password reset via email verification
- [ ] Session timeout and auto-logout

## 📊 Performance Monitoring & Analytics

### 10. Telemetry & Monitoring
**Status**: 📋 Planned  
**Impact**: Medium (performance insights, debugging)
- [ ] Implement comprehensive logging with rolling files (`logs/agent-%d.log`)
- [ ] Add JavaFX Pulse Logger integration (`com.sun.javafx.pulseLogger=true`)
- [ ] JFR profiling integration for production monitoring
- [ ] Performance metrics dashboard for key operations
- [ ] User behavior analytics (privacy-respecting)

## 🎯 Success Metrics

### Performance Targets
- **Startup Time**: ≤ 1.5s (currently unknown)
- **Task Loading**: ≤ 16ms per 100 tasks (currently 37ms per cell)
- **Frame Rate**: 60 FPS sustained (≤ 16ms pulse time)
- **Memory Usage**: < 200MB for 1000+ tasks
- **Database Operations**: < 10ms for single queries

### Quality Gates
- **Test Coverage**: ≥ 80% (currently 62%)
- **Accessibility**: WCAG-AA compliance (4.5:1 contrast ratio)
- **Security**: No hardcoded credentials or plaintext storage
- **Performance**: No operation > 100ms on typical hardware

## 🚦 Implementation Priority Matrix

### 🔴 Critical (Do First)
1. Database query optimization (task-db-optimization.md)
2. TaskListCell performance fix
3. WCAG contrast compliance

### 🟡 Important (Do Soon)
4. Memory management and lazy loading
5. Enhanced data encryption
6. Test coverage improvements

### 🟢 Nice to Have (Do Later)
7. Advanced features (AI, analytics)
8. Build system improvements
9. Collaborative features

---

## 💡 Implementation Notes

- **Resource Constraints**: Minimize paid API requests while maintaining quality
- **Quality First**: Never compromise quality for performance
- **Incremental Delivery**: Each task should be independently deployable
- **Backward Compatibility**: Maintain compatibility with existing data
- **User-Centric**: Every change should improve user experience

## 📋 Task Template Usage

For each new task, copy `lib/markdown.md` template and fill in:
- `{{TASK_TITLE}}`: Descriptive name
- `{{DATE}}`: Current date  
- `{{TASK_SLUG}}`: Kebab-case identifier
- `{{ONE_LINE_GOAL}}`: Clear objective

Always create branch: `bot/{{TASK_SLUG}}` and open PR with `[bot]` prefix.
