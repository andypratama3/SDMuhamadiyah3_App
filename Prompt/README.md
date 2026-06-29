# EduOcto AI-OS — AI Development Kit

> The permanent engineering foundation for **EduOcto**, a Kotlin Multiplatform School ERP
> (Android · iOS · Desktop · future Web), built on Compose Multiplatform, Material 3, Ktor, and SQLDelight.

This kit is designed to be read by **both humans and AI coding agents** (e.g. OpenCode, Claude Code,
Cursor, Copilot Workspace). Every document is self-contained, explicit, and free of placeholders.
An agent should be able to open any single file and act on it correctly without needing the rest of
the repository for context — though reading `00-AI-CONSTITUTION.md` first is mandatory.

## Where Are We Right Now?

**`TRACKER.md`** is the living status board — every task, its docs, its status, platform
verification, and any flagged conflicts. Check it first if you're picking work back up. To drive a
full continuous build session task-by-task through the whole backlog, use
**`Prompt/FULL_BUILD_PROMPT.md`**.

## How an AI agent should use this kit

1. **Always load `00-AI-CONSTITUTION.md` first.** It is the binding contract for every other file.
2. **Load `01-PROJECT-CONTEXT.md`** to understand what EduOcto is and who uses it.
3. When building a **screen** → read `21-KMP-STANDARDS.md`, `22-COMPOSE-STANDARDS.md`,
   `23-CLEAN-ARCHITECTURE.md`, the relevant `Blueprints/*.md`, and the relevant `ERP/*.md`.
4. When building a **component** → read `07-COMPONENT-SYSTEM.md`, `20-COMPONENT-LIBRARY.md`,
   `08-GLASSMORPHISM.md`, `09-ANIMATION.md`.
5. When writing a **prompt for itself or a sub-agent** → use `Prompt/*.md` templates verbatim.
6. Before marking work "done" → run every relevant `Checklists/*.md`.
7. Never skip a section, never write `TODO`, never say "implementation omitted." If something is
   genuinely out of scope, say so explicitly and explain why — silence and placeholders are forbidden.

## Index

### Foundation
| File | Purpose |
|---|---|
| `00-AI-CONSTITUTION.md` | Non-negotiable rules every agent and engineer must follow |
| `01-PROJECT-CONTEXT.md` | What EduOcto is, who it's for, current status |
| `02-DESIGN-PHILOSOPHY.md` | Why EduOcto looks and feels the way it does |

### Design System
| File | Purpose |
|---|---|
| `03-DESIGN-SYSTEM.md` | The design system as a whole — tokens, layers, principles |
| `04-COLOR-SYSTEM.md` | Full color palette, roles, contrast rules |
| `05-TYPOGRAPHY.md` | Type scale, font usage, rhythm |
| `06-SPACING-SYSTEM.md` | 8dp grid, radius scale, elevation |
| `07-COMPONENT-SYSTEM.md` | Component anatomy and composition rules |
| `08-GLASSMORPHISM.md` | The "Premium Glass" surface system |
| `09-ANIMATION.md` | Motion system, easing, durations |
| `10-HAPTICS.md` | Haptic feedback standards per platform |
| `11-NAVIGATION.md` | Navigation architecture and patterns |

### UI Pattern Standards
| File | Purpose |
|---|---|
| `12-DASHBOARD-STANDARDS.md` | How every dashboard must be built |
| `13-FORM-STANDARDS.md` | Form layout, validation, submission UX |
| `14-LIST-STANDARDS.md` | Lists, lazy lists, pagination |
| `15-TABLE-STANDARDS.md` | Data tables / fintech-style data grids |
| `16-DIALOG-STANDARDS.md` | Dialogs, sheets, confirmations |
| `17-EMPTY-STATES.md` | Empty state design |
| `18-LOADING-STATES.md` | Loading & skeleton standards |
| `19-ERROR-STATES.md` | Error UX and recovery patterns |
| `20-COMPONENT-LIBRARY.md` | Canonical component inventory |

### Engineering
| File | Purpose |
|---|---|
| `21-KMP-STANDARDS.md` | Kotlin Multiplatform module & target rules |
| `22-COMPOSE-STANDARDS.md` | Compose Multiplatform coding rules |
| `23-CLEAN-ARCHITECTURE.md` | Layering, modules, dependency rules |
| `24-STATE-MANAGEMENT.md` | MVI/MVVM, UiState, side effects |
| `25-PERFORMANCE.md` | Recomposition, memory, cold start |
| `26-ACCESSIBILITY.md` | WCAG AA compliance for KMP/Compose |
| `27-SECURITY.md` | AuthN/AuthZ, storage, transport, logging |
| `28-CODING-STANDARDS.md` | Kotlin style, naming, file layout |
| `29-REFACTORING-RULES.md` | When and how to refactor safely |
| `30-FULL-OUTPUT-ENFORCEMENT.md` | Anti-laziness contract for AI agents |

### ERP Domain (`ERP/`)
Dashboard · Student · Teacher · Attendance · Finance · Payment · ReportCard · Library ·
Inventory · Announcement · Notification · PPDB · Calendar · Settings · Analytics

### Prompt Library (`Prompt/`)
MASTER_PROMPT · SCREEN_PROMPT · COMPONENT_PROMPT · REFACTOR_PROMPT · BUGFIX_PROMPT ·
PERFORMANCE_PROMPT · ACCESSIBILITY_PROMPT · ANIMATION_PROMPT · CODE_REVIEW_PROMPT

### Checklists (`Checklists/`)
UI · UX · Performance · Design System · Release · Screen

### Blueprints (`Blueprints/`)
Dashboard · Finance · Student · Teacher · Payment · Attendance · Analytics · Profile

### Examples (`Examples/`)
Login · Dashboard · Form · List · Detail · Component

## Versioning

This kit is versioned independently from the app. Bump the version below whenever a standard
changes, and note the change in `CHANGELOG` (create one once the first real revision happens).

```
EduOcto AI Development Kit — v1.0.0
Status: Living document — authoritative as of creation date
```
