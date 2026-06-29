# ERP — Teacher Module

## Purpose

Manages teacher/staff academic profiles: subject assignments, class/homeroom assignments, schedule,
and workload — distinct from `ERP/Staff` (administrative employment record) though they reference
the same underlying person.

## Domain Model

```kotlin
data class Teacher(
    val id: String,
    val fullName: String,
    val nip: String?,                  // government teacher ID, if applicable
    val email: String,
    val phone: String,
    val subjects: List<SubjectAssignment>,
    val homeroomClassId: String?,      // null if not a homeroom teacher
    val photoUrl: String?,
)

data class SubjectAssignment(
    val subjectId: String,
    val subjectName: String,
    val classId: String,
    val weeklyHours: Int,
)

data class ScheduleSlot(
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val classId: String,
    val subjectId: String,
    val room: String?,
)
```

## Business Rules

1. **A homeroom teacher is responsible for exactly one class per academic year** — enforced at
   assignment time (cannot assign a second homeroom class to a teacher already assigned one for the
   same academic year without first reassigning the existing one).
2. **Subject assignments determine attendance/grading permission scope** — a Teacher can only submit
   Attendance (`ERP/Attendance.md`) or grades (`ERP/ReportCard.md`) for classes/subjects they are
   explicitly assigned to teach in the current academic year.
3. **Schedule conflicts are validated on assignment**: the system rejects creating a `ScheduleSlot`
   that overlaps an existing slot for the same teacher or the same class/room.
4. **Workload visibility**: Principal/Vice Principal can view aggregate weekly hours per teacher to
   identify over/under-allocation — this view is read-only and informational, not an enforcement gate.

## Screens

- Teacher List (Principal/Admin view) — roster with subject/class assignment summary.
- Teacher Detail — profile, full schedule, assigned classes, workload summary.
- My Schedule (Teacher's own view) — today/week view, see `11-NAVIGATION.md` "Classes" tab.
- Assignment management (Admin) — assign subjects/classes/homeroom for an academic year.

## Permission Matrix

| Action | Principal/VP | Admin | Teacher (self) | Other Teacher | Finance | Student/Parent |
|---|---|---|---|---|---|---|
| View own schedule/assignments | ✅ | ✅ | ✅ | ❌ | ❌ | ➖ |
| View all teachers' schedules | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| Create/edit subject/class assignment | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| View teacher contact info for their child's class | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ (own child's teachers only) |

## Audit Requirements

Assignment changes (subject, class, homeroom) are audited (actor, timestamp, before/after) — these
affect grading/attendance authority and downstream academic record integrity.
