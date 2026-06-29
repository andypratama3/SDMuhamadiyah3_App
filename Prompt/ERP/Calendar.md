# ERP — Calendar Module

## Purpose

Centralizes the school's academic calendar (terms, holidays, exam periods) and event scheduling
(meetings, deadlines) — the reference all other modules' date-sensitive logic (Attendance terms,
ReportCard term boundaries, PPDB key dates, Finance billing periods) should consume rather than
hardcoding term boundaries.

## Domain Model

```kotlin
data class CalendarEvent(
    val id: String,
    val title: String,
    val description: String?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val type: CalendarEventType,
    val audience: AnnouncementAudience,  // reuses ERP/Announcement.md's audience model
    val isAllDay: Boolean,
)
enum class CalendarEventType { Holiday, ExamPeriod, TermBoundary, Meeting, Deadline, SchoolEvent }

data class AcademicTermDefinition(
    val academicYear: String,
    val term: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
```

## Business Rules

1. **`AcademicTermDefinition` is the single source of truth for term boundaries** — `ERP/Finance.md`
   billing periods, `ERP/ReportCard.md` term grouping, and `ERP/Attendance.md` rate calculations all
   reference it; none of them store or compute term start/end independently.
2. **Holidays/`ExamPeriod` affect Attendance expectations** — sessions are not expected (and thus not
   counted as `Absent` if unmarked) on declared `Holiday` dates; `ExamPeriod` may use a different
   attendance-marking mode per school policy (configurable, not hardcoded).
3. **Conflicting events** (two `Meeting`s same room/time) are flagged as a soft warning to the
   creator, not hard-blocked — mirrors the PPDB capacity-warning pattern (judgment-allowing, not a
   hard system constraint).

## Screens

- Calendar View (all roles, audience-filtered) — month/week/list view toggle.
- Event creation (Principal/VP/Admin/Teacher, scoped by role to allowed audiences, same pattern as
  `ERP/Announcement.md`).
- Academic Year Setup (Principal/Admin) — define terms, holidays for the year, typically a once-per-
  year administrative task.

## Permission Matrix

| Action | Principal/VP | Admin | Teacher | Finance | Student/Parent |
|---|---|---|---|---|---|
| Define academic year/terms/holidays | ✅ | ✅ | ❌ | ❌ | ❌ |
| Create event (own scope) | ✅ | ✅ | ✅ (own class) | ❌ | ❌ |
| View calendar | ✅ | ✅ | ✅ | ✅ | ✅ |

## Audit Requirements

Term/holiday definition changes are audited (these affect Attendance and Finance calculations
retroactively if changed mid-year, which should be flagged as a high-impact action requiring
confirmation, per `16-DIALOG-STANDARDS.md`).
