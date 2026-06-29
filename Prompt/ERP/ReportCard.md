# ERP — Report Card Module

## Purpose

Manages grade entry, term-based academic assessment, and the generated report card (rapor) document
delivered to students/parents at the end of each term.

## Domain Model

```kotlin
data class Grade(
    val id: String,
    val studentId: String,
    val subjectId: String,
    val term: AcademicTerm,
    val componentType: GradeComponent,  // e.g. Daily, MidTerm, Final, Practical
    val score: Double,                  // 0–100 scale
    val enteredBy: String,              // teacherId
    val enteredAt: Instant,
)
enum class GradeComponent { Daily, Assignment, MidTerm, Final, Practical, Attitude }

data class AcademicTerm(val academicYear: String, val term: Int) // term 1 or 2 (semester)

data class ReportCard(
    val studentId: String,
    val term: AcademicTerm,
    val subjectGrades: List<SubjectFinalGrade>,
    val attendanceSummary: AttendanceSummaryForTerm,
    val homeroomNote: String?,
    val status: ReportCardStatus,
    val releasedAt: Instant?,
)
data class SubjectFinalGrade(val subjectId: String, val finalScore: Double, val letterGrade: String, val teacherNote: String?)
enum class ReportCardStatus { Draft, PendingHomeroomReview, Finalized, Released }
```

## Business Rules

1. **Final score calculation per subject is a weighted formula** defined per school policy (e.g.
   Daily 20% + Assignment 20% + MidTerm 25% + Final 35%) stored as a configurable `GradingPolicy`
   (`ERP/Settings.md`) — never hardcoded weights inside UI/ViewModel code; computed once in
   `CalculateFinalGradeUseCase`.
2. **Only the assigned subject teacher enters grades for that subject** (per `ERP/Teacher.md` rule
   2). Homeroom Teacher reviews the compiled `ReportCard` across all subjects before it can be
   finalized — `PendingHomeroomReview → Finalized` requires explicit homeroom sign-off, not automatic
   progression once all subject grades exist.
3. **Report cards are never visible to Student/Parent until `Released`.** `Finalized` is an internal
   state allowing Principal/Admin a final check before the scheduled release date/time — this
   prevents grades leaking before the school's intended release moment.
4. **Grade edits after `Finalized`** require the same co-approval pattern as Attendance corrections
   (`ERP/Attendance.md` rule 4) — Homeroom + Admin/Principal, always audited, since a finalized grade
   change affects an officially issued document.
5. **Letter grade mapping** (e.g. 90–100 = A) is a configurable scale per `ERP/Settings.md`, not a
   hardcoded threshold, since schools may use different scales.

## Screens

- Grade Entry (Subject Teacher) — gradebook-style table per class/subject/term, see
  `15-TABLE-STANDARDS.md`.
- Report Card Review (Homeroom Teacher) — compiled view across subjects, sign-off action.
- Report Card Release (Admin/Principal) — schedule and trigger release for a class/term.
- Report Card View (Student/Parent) — read-only, downloadable as PDF, see `Blueprints/AnalyticsBlueprint.md`
  for any trend visualization alongside it.

## Permission Matrix

| Action | Principal/VP | Homeroom | Subject Teacher | Admin | Parent | Student |
|---|---|---|---|---|---|---|
| Enter grades (own subject) | ❌ | ✅ (if also teaching) | ✅ | ❌ | ❌ | ❌ |
| Review/sign off compiled report card | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ |
| Finalize/Release report cards | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ |
| Edit finalized grade (co-approval) | ✅ | ✅ | ❌ | ✅ | ❌ | ❌ |
| View own/child's released report card | ➖ | ➖ | ➖ | ➖ | ✅ | ✅ |

## Audit Requirements

Every grade entry and edit logs actor/timestamp/before-after; report card status transitions
(`Finalized`, `Released`) log actor and timestamp — these are official academic records.
