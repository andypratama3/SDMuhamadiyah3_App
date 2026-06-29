# ERP — Dashboard Module

## Purpose

The Dashboard module provides each role a single entry screen summarizing what matters to them today.
It has no independent data model of its own — it aggregates read-only views across other modules
(Attendance, Finance, PPDB, Announcement). See `12-DASHBOARD-STANDARDS.md` for the UI contract this
module must satisfy.

## Domain Model

```kotlin
data class DashboardSummary(
    val heroStat: HeroStat,
    val secondaryStats: List<SecondaryStat>,
    val actionableItems: List<ActionableItem>,
    val recentActivity: List<ActivityEvent>,
)

data class HeroStat(val label: String, val value: String, val trend: Trend? = null)
data class SecondaryStat(val label: String, val value: String, val icon: String, val tone: StatTone)
data class ActionableItem(val id: String, val title: String, val subtitle: String, val destination: Destination, val tone: StatTone)
data class ActivityEvent(val id: String, val description: String, val timestamp: Instant, val actorName: String?)
enum class StatTone { Neutral, Success, Warning, Danger }
data class Trend(val direction: TrendDirection, val percent: Double)
enum class TrendDirection { Up, Down, Flat }
```

## Per-Role Aggregation Rules

| Role | Hero Stat Source | Secondary Stats | Actionable Items Source |
|---|---|---|---|
| Principal | `Attendance` (school-wide % today) | Total students, Total staff, SPP collected this month, PPDB applications this week | Pending approvals (leave, PPDB final sign-off), Low-attendance classes |
| Vice Principal | Same as Principal, scoped to assigned grade level(s) | Same, scoped | Same, scoped |
| Teacher | `Attendance` (today's taught classes summary) | Classes today, Ungraded items, Unread messages | Attendance not yet submitted, Assignments pending grading |
| Homeroom Teacher | Homeroom class attendance % | Students with 3+ absences this month, Pending parent messages | Follow-up required (chronic absence, failing grade trend) |
| Admin Staff | New PPDB applications this week | Documents pending verification, Active students, Pending data corrections | PPDB applications needing review, Document verification queue |
| Finance Staff | Total SPP outstanding this month | Collected this month, Overdue count, Payroll run status | Overdue invoices (top N by amount), Payroll pending approval |
| Student | Today's schedule (next class) | Attendance % this term, Unread announcements | Upcoming assignment/exam, Unread teacher message |
| Parent | Selected child's outstanding balance | Attendance % this term, Latest grade update | Unpaid invoice, Unread message from teacher |
| Guest | PPDB intake status/dates | Application progress (if started) | Continue application, View requirements |

## Business Rules

1. Each section of `DashboardSummary` is fetched and cached independently (see
   `12-DASHBOARD-STANDARDS.md` loading rule) — a `DashboardRepository` exposes one `Flow` per
   section, not one monolithic call.
2. A Parent with multiple children sees an aggregated hero stat (total outstanding across all
   children) plus a child-switcher to drill into a single child's full dashboard.
3. "Recent activity" never surfaces another student's/family's personal data to a Parent/Student role
   — visibility is filtered server-side by the requesting user's role and relationship, not just
   hidden client-side.

## Permission Matrix

| Action | Principal | VP | Teacher | Homeroom | Admin | Finance | Student | Parent | Guest |
|---|---|---|---|---|---|---|---|---|---|
| View school-wide stats | ✅ | ✅ (scoped) | ❌ | ❌ | ✅ | ✅ (financial) | ❌ | ❌ | ❌ |
| View own class/assigned stats | ✅ | ✅ | ✅ | ✅ | ➖ | ➖ | ➖ | ➖ | ❌ |
| View own/child's data only | ➖ | ➖ | ➖ | ➖ | ➖ | ➖ | ✅ | ✅ | ❌ |

(✅ = full access, ➖ = not applicable to role's scope, ❌ = explicitly denied)
