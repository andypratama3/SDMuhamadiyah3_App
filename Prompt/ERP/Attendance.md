# ERP — Attendance Module

## Purpose

Records daily attendance per student per class session, the basis for attendance-rate KPIs across
Dashboard, ReportCard, and homeroom follow-up workflows.

## Domain Model

```kotlin
data class AttendanceRecord(
    val id: String,
    val studentId: String,
    val classId: String,
    val date: LocalDate,
    val status: AttendanceStatus,
    val note: String?,                 // e.g. "Surat dokter" for Sick
    val recordedBy: String,            // teacherId
    val recordedAt: Instant,
)

enum class AttendanceStatus { Present, Sick, Excused, Absent, Late }
```

## Business Rules

1. **One record per student per class-session per day.** Re-submitting attendance for the same
   student/date/class updates the existing record (with audit trail of the change), it never creates
   a duplicate.
2. **Only the assigned teacher for that class/subject/time-slot may submit attendance** for a given
   session (see `ERP/Teacher.md` rule 2) — Homeroom Teacher may additionally submit/correct daily
   attendance for their homeroom class regardless of subject, to cover whole-day absence tracking.
3. **Late vs. Absent threshold** is configurable per school policy (e.g. arriving >15 minutes after
   session start = `Late`; no arrival at all = `Absent`) — stored as a `SchoolPolicy` setting
   (`ERP/Settings.md`), not hardcoded.
4. **Editing past attendance** beyond a configurable window (default: 3 days) requires Homeroom
   Teacher + Admin/Principal co-approval and is always audited — prevents retroactive manipulation of
   attendance records that may affect report cards already issued.
5. **Chronic absence flag**: a student with ≥3 unexcused absences (`Absent`) within a rolling 30-day
   window is automatically surfaced in the Homeroom Teacher's and Parent's actionable items
   (`ERP/Dashboard.md`).
6. **Attendance % calculation**: `Present + Late` (Late counts as attended for rate purposes, flagged
   separately for punctuality reporting) divided by total scheduled sessions in the period, excluding
   `Excused` from the denominator if school policy defines excused absences as non-penalized — this
   exact formula is defined once in `CalculateAttendanceRateUseCase` (`23-CLEAN-ARCHITECTURE.md`
   pattern), never recomputed ad hoc per screen.

## Screens

- Mark Attendance (Teacher, per class session) — roster list with quick-tap status per student,
  bulk "mark all present" then exceptions, see `14-LIST-STANDARDS.md` swipe actions.
- Attendance History (Student/Parent view) — calendar + list view of own/child's record.
- Class Attendance Report (Homeroom/Admin/Principal) — aggregate view, exportable.
- Today's Attendance Summary (Dashboard widget, all roles per `ERP/Dashboard.md`).

## Permission Matrix

| Action | Principal/VP | Homeroom | Subject Teacher | Admin | Finance | Student | Parent |
|---|---|---|---|---|---|---|---|
| Mark attendance (own class/subject) | ❌ | ✅ (homeroom) | ✅ (assigned subject) | ❌ | ❌ | ❌ | ❌ |
| Edit past attendance (within window) | ❌ | ✅ | ✅ (own records) | ❌ | ❌ | ❌ | ❌ |
| Edit past attendance (beyond window) | ✅ (co-approve) | ✅ (co-approve) | ❌ | ✅ (co-approve) | ❌ | ❌ | ❌ |
| View class/school attendance reports | ✅ | ✅ (own class) | ❌ | ✅ | ❌ | ❌ | ❌ |
| View own/child's attendance | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ | ✅ |

## Audit Requirements

Every create/edit on `AttendanceRecord` logs actor, timestamp, before/after status — mandatory given
its downstream effect on report cards and chronic-absence flags.
