# ERP — Settings Module

## Purpose

Houses school-wide configuration (policy values referenced by other modules) and per-user
preferences — the two are architecturally distinct and must not be conflated.

## Domain Model

```kotlin
data class SchoolPolicy(
    val attendanceLateThresholdMinutes: Int,
    val attendanceEditWindowDays: Int,
    val gradingPolicy: GradingPolicy,
    val libraryMaxConcurrentLoans: Map<BorrowerType, Int>,
    val invoiceGenerationLeadDays: Int,
    val ppdbCapacityByGradeLevel: Map<Int, Int>,
)
data class GradingPolicy(
    val componentWeights: Map<GradeComponent, Double>,  // must sum to 1.0
    val letterGradeScale: List<LetterGradeBand>,
)
data class LetterGradeBand(val minScore: Double, val maxScore: Double, val letter: String)

data class UserPreferences(
    val userId: String,
    val locale: String,                // "id" or "en"
    val theme: ThemePreference,        // currently only Light; structure supports Dark later
    val notificationPreferences: List<NotificationPreference>, // see ERP/Notification.md
)
enum class ThemePreference { Light, Dark, System }

data class UserAccount(
    val id: String,
    val role: UserRole,
    val isActive: Boolean,
)
enum class UserRole { Principal, VicePrincipal, Teacher, HomeroomTeacher, AdminStaff, FinanceStaff, Student, Parent, Guest }
```

## Business Rules

1. **`SchoolPolicy` is a singleton per school** (or per-school-tenant in the future multi-school
   model, see `01-PROJECT-CONTEXT.md` non-goals) — every module reads it rather than embedding its
   own default constants.
2. **`GradingPolicy.componentWeights` must sum to exactly 1.0** — validated on save; the save action
   is rejected client-side and server-side if weights don't sum correctly.
3. **Policy changes affecting financial/academic calculations** (grading weights, attendance late
   threshold) take effect only for *future* calculations by default; retroactively recalculating
   historical records requires an explicit, separately-confirmed action (never silent).
4. **Role/permission management** (`UserAccount.role`, `isActive`) is Principal/Admin-only and is the
   single mechanism by which every Permission Matrix across `ERP/*.md` is actually enforced at
   runtime — see `27-SECURITY.md`.

## Screens

- School Policy Settings (Principal/Admin) — grouped by domain (Attendance, Grading, Library, PPDB).
- User Management (Principal/Admin) — list/create/deactivate accounts, assign roles.
- My Preferences (all roles) — locale, notification toggles.

## Permission Matrix

| Action | Principal/VP | Admin | All other roles |
|---|---|---|---|
| Edit school policy | ✅ | ✅ | ❌ |
| Manage user accounts/roles | ✅ | ✅ | ❌ |
| Edit own preferences | ✅ | ✅ | ✅ |

## Audit Requirements

Every `SchoolPolicy` change and every `UserAccount.role`/`isActive` change is audited (actor,
timestamp, before/after) — these are the highest-leverage configuration points in the system.
