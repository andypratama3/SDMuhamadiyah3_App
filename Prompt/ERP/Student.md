# ERP — Student Module

## Purpose

Manages the student master record: identity, enrollment, class assignment, guardian linkage, and
document records. This is the most-referenced entity in the system — Attendance, Finance, Payment,
ReportCard, and PPDB (on acceptance) all key off `Student.id`.

## Domain Model

```kotlin
data class Student(
    val id: String,
    val nisn: String,              // Indonesian national student ID
    val fullName: String,
    val nickname: String?,
    val gender: Gender,
    val dateOfBirth: LocalDate,
    val classId: String,
    val enrollmentDate: LocalDate,
    val status: StudentStatus,
    val guardians: List<GuardianLink>,
    val address: Address,
    val photoUrl: String?,
)

enum class Gender { Male, Female }
enum class StudentStatus { Active, Inactive, Graduated, Transferred, Dropped }

data class GuardianLink(
    val guardianId: String,
    val relationship: GuardianRelationship,
    val isPrimaryContact: Boolean,
    val isFinanciallyResponsible: Boolean,
)
enum class GuardianRelationship { Father, Mother, LegalGuardian, Other }

data class ClassAssignment(
    val classId: String,
    val className: String,         // e.g. "5A"
    val gradeLevel: Int,
    val homeroomTeacherId: String,
    val academicYear: String,      // e.g. "2026/2027"
)
```

## Business Rules

1. **NISN is unique and immutable** once a student record is created — corrections go through an
   explicit "Data Correction" audited workflow (Admin Staff role), never a direct field edit on a
   record that has any associated Attendance/Payment/ReportCard history.
2. **A student has exactly one active `classId`** per academic year. Class transfer mid-year creates
   a `ClassTransferRecord` (effective date, from-class, to-class, reason) rather than overwriting
   history — Attendance/grades before the transfer remain attributed to the original class.
3. **Status transitions are constrained:** `Active → Inactive/Graduated/Transferred/Dropped` only.
   A `Graduated`/`Dropped` student cannot be reactivated directly — re-enrollment creates a new
   student record linked to the old one via a `previousStudentId` reference, preserving historical
   integrity.
4. **At least one guardian must be `isFinanciallyResponsible = true`** at all times — Finance module
   relies on this to route SPP invoices; the system blocks removing the last financially-responsible
   guardian without designating a replacement first.
5. **Minors' data visibility** is strictly role/relationship scoped — see Permission Matrix. A
   Parent only ever sees students where their `guardianId` appears in that student's `guardians` list.

## Screens

- Student List (role-scoped: full roster for Admin/Principal; own class for Teacher/Homeroom; own
  children for Parent; self for Student) — see `14-LIST-STANDARDS.md`, `Blueprints/StudentBlueprint.md`.
- Student Detail — profile, guardians, class history, quick links to Attendance/Payment/ReportCard
  for that student.
- Add/Edit Student form — see `13-FORM-STANDARDS.md`.
- Class Transfer flow — guided, audited (per `27-SECURITY.md`).

## Permission Matrix

| Action | Principal/VP | Admin | Homeroom | Teacher | Finance | Parent | Student |
|---|---|---|---|---|---|---|---|
| View full roster | ✅ | ✅ | ❌ | ❌ | ✅ (financial fields only) | ❌ | ❌ |
| View own class roster | ✅ | ✅ | ✅ | ✅ (assigned classes) | ❌ | ❌ | ❌ |
| View own/child profile | ✅ | ✅ | ✅ (own class) | ➖ | ➖ | ✅ (own children) | ✅ (self) |
| Create/Edit student record | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Class transfer | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Change status (graduate/drop) | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |

## Audit Requirements

Every create/edit/status-change/class-transfer on a `Student` record writes an audit entry (actor,
timestamp, field-level before/after) per `27-SECURITY.md`.
