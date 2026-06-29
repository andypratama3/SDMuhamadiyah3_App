# EduOcto Build Tracker

**This file is the single source of truth for "where are we."** Every task below maps to one or more
docs in this kit. The agent (OpenCode) updates this file — status, platform verification, and notes
— immediately after finishing each task, in the same change that delivers the work. Never batch
multiple tasks' tracker updates into one later edit.

## Status Legend

`⬜ Not Started` · `🔄 In Progress` · `✅ Done` · `⚠️ Blocked/Flagged` (see Notes for reason)

## How to Update This File (binding — see `00-AI-CONSTITUTION.md` rule 3 "Document")

After finishing a task:
1. Change its Status.
2. Fill in **Platforms Verified** (`A` = Android, `I` = iOS, `D` = Desktop — only mark a letter once
   actually built/verified for that target, per `21-KMP-STANDARDS.md`).
3. Fill in **Checklist Run** with which `Checklists/*.md` were used.
4. Add a one-line **Note** for anything flagged, deferred, or assumption-based (per
   `30-FULL-OUTPUT-ENFORCEMENT.md` — no silent gaps).
5. If a task is blocked, set status to `⚠️` and state the blocker — do not skip silently to the next
   task without recording why.

---

## Phase 0 — Foundation (pre-Constitution work + Design System)

| # | Task | Docs | Status | Platforms | Checklist Run | Notes |
|---|---|---|---|---|---|---|
| P7 | Inter font integration | `05-TYPOGRAPHY.md` | ✅ Done | A, I | — | Completed prior to kit adoption |
| P8 | SQLDelight offline-first data layer | `21-KMP-STANDARDS.md` | ✅ Done | A, I | — | Completed prior to kit adoption |
| P9 | Design System Foundation (EduOctoTheme, Colors, Typography, Spacing, Shapes, Motion, GlassSurface) | `03`–`06`, `08`, `09` | ✅ Done | I | `DESIGN_SYSTEM_CHECKLIST.md` | Android not yet verified — see P9.5a |
| P9.5a | Verify Design System Foundation on Android (incl. `supportsBackdropBlur()` branch) | `08-GLASSMORPHISM.md`, `21-KMP-STANDARDS.md` | ⬜ Not Started | — | `DESIGN_SYSTEM_CHECKLIST.md` | Blocking — must close before any screen work |
| P9.5b | Migrate `com.sdm3.parent.core.designsystem.theme` (Sdm3*/SDM3*) → `com.eduocto.designsystem` (EduOcto*), delete old package | `29-REFACTORING-RULES.md`, `20-COMPONENT-LIBRARY.md` | ⬜ Not Started | — | — | Flagged conflict from P9 report |

## Phase 1 — App Shell

| # | Task | Docs | Status | Platforms | Checklist Run | Notes |
|---|---|---|---|---|---|---|
| P10 | Navigation graph: typed `Destination` sealed interface, back-stack-per-tab pattern | `11-NAVIGATION.md` | ⬜ | | | |
| P11 | `AppSessionState` holder (current user/role/locale), `UserRole` wiring | `24-STATE-MANAGEMENT.md`, `ERP/Settings.md` | ⬜ | | | |
| P12 | Auth/Login screen (full ViewModel/UiState/Content/Screen/Previews) | `Examples/LoginExample.md`, `13-FORM-STANDARDS.md`, `27-SECURITY.md` | ⬜ | | | |
| P13 | Role-based navigation shell (bottom nav / nav rail per role table) | `11-NAVIGATION.md` (top-level destinations table) | ⬜ | | | |

## Phase 2 — Dashboard

| # | Task | Docs | Status | Platforms | Checklist Run | Notes |
|---|---|---|---|---|---|---|
| P14 | Dashboard shell — independent per-section loading, per-role hero/actionable items | `12-DASHBOARD-STANDARDS.md`, `ERP/Dashboard.md`, `Blueprints/DashboardBlueprint.md`, `Examples/DashboardExample.md` | ⬜ | | | |

## Phase 3 — Core ERP Modules (dependency-ordered: Student first — everything else references it)

| # | Task | Docs | Status | Platforms | Checklist Run | Notes |
|---|---|---|---|---|---|---|
| P15 | Student module — list, detail, add/edit form, class transfer | `ERP/Student.md`, `Blueprints/StudentBlueprint.md`, `Examples/ListExample.md`, `Examples/DetailExample.md` | ⬜ | | | |
| P16 | Teacher module — schedule, directory, assignment management | `ERP/Teacher.md`, `Blueprints/TeacherBlueprint.md` | ⬜ | | | |
| P17 | Attendance module — mark attendance, history, chronic-absence flag | `ERP/Attendance.md`, `Blueprints/AttendanceBlueprint.md` | ⬜ | | | |
| P18 | Finance module — fee structures, invoices, financial reports, payroll | `ERP/Finance.md`, `Blueprints/FinanceBlueprint.md` | ⬜ | | | |
| P19 | Payment module — record payment, payment history, reconciliation | `ERP/Payment.md`, `Blueprints/PaymentBlueprint.md`, `Examples/FormExample.md` | ⬜ | | | |
| P20 | Report Card module — grade entry, homeroom review, release | `ERP/ReportCard.md` | ⬜ | | | |
| P21 | PPDB module — public landing, application form, status tracker, review/verification | `ERP/PPDB.md` | ⬜ | | | |
| P22 | Announcement module — compose, feed, detail | `ERP/Announcement.md` | ⬜ | | | |
| P23 | Notification module — center, settings, deep-link wiring | `ERP/Notification.md` | ⬜ | | | |
| P24 | Calendar module — calendar view, event creation, academic year setup | `ERP/Calendar.md` | ⬜ | | | |
| P25 | Library module — catalog, loan management, my loans | `ERP/Library.md` | ⬜ | | | |
| P26 | Inventory module — item list, stock in/out, disposal approval | `ERP/Inventory.md` | ⬜ | | | |
| P27 | Settings module — school policy, user management, preferences | `ERP/Settings.md` | ⬜ | | | |
| P28 | Analytics module — dashboard, metric detail/drill-down | `ERP/Analytics.md`, `Blueprints/AnalyticsBlueprint.md` | ⬜ | | | |
| P29 | Profile screen — all roles | `Blueprints/ProfileBlueprint.md` | ⬜ | | | |

## Phase 4 — Cross-Cutting Hardening (run once Phase 3 is fully ✅)

| # | Task | Docs | Status | Platforms | Checklist Run | Notes |
|---|---|---|---|---|---|---|
| P30 | Full accessibility audit across every screen built so far | `26-ACCESSIBILITY.md`, `Prompt/ACCESSIBILITY_PROMPT.md` | ⬜ | | | |
| P31 | Full performance audit across every list/data-heavy screen | `25-PERFORMANCE.md`, `Prompt/PERFORMANCE_PROMPT.md` | ⬜ | | | |
| P32 | Full security audit — Finance/PPDB/Attendance audit logging, permission enforcement | `27-SECURITY.md` | ⬜ | | | |
| P33 | Release checklist run | `Checklists/RELEASE_CHECKLIST.md` | ⬜ | | | |

---

## Summary

- **Total tasks:** 33 (4 done, 29 remaining)
- **Current phase:** Phase 0 (closing out) — P9.5a and P9.5b must complete before Phase 1 begins
- **Next task to pick up:** P9.5a

## Conflict/Flag Log (cumulative — never delete entries, only mark resolved)

| Raised in | Description | Status |
|---|---|---|
| P9 | Two parallel design systems (`com.eduocto.designsystem` vs `com.sdm3.parent.core.designsystem.theme`) | ⚠️ Open — being resolved in P9.5b |
