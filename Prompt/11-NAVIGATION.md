# 11 — Navigation

## Architecture

EduOcto uses a single navigation graph defined in `commonMain`, driven by type-safe destinations
(sealed classes/objects), shared across Android, iOS, and Desktop via Compose Multiplatform
Navigation. Platform shells (`androidApp`, `iosApp`) host the same navigation graph; they do not
define their own.

```kotlin
@Serializable
sealed interface Destination {
    @Serializable data object Login : Destination
    @Serializable data object Dashboard : Destination
    @Serializable data class StudentDetail(val studentId: String) : Destination
    @Serializable data class PaymentDetail(val invoiceId: String) : Destination
    @Serializable data object AttendanceToday : Destination
    @Serializable data class ReportCard(val studentId: String, val term: String) : Destination
    // ... one entry per screen, never a raw String route
}
```

**Rule:** routes are never raw strings (`"student/{id}"`). Use typed destinations with
`kotlinx.serialization` so arguments are compile-time safe across all targets.

## Navigation Shell by Window Size

| Window Size | Pattern |
|---|---|
| Compact (phone) | Bottom navigation bar (4–5 top-level destinations) + stack navigation per tab |
| Medium (small tablet) | Navigation rail (icons + labels) replacing bottom bar |
| Expanded (tablet landscape/desktop) | Persistent navigation rail/drawer + list-detail two-pane layout where applicable (e.g. Student list ↔ Student detail) |

## Top-Level Destinations (per role — see `ERP/*.md` for full detail)

| Role | Top-level tabs |
|---|---|
| Principal/Vice Principal | Dashboard, Analytics, Approvals, Reports, Settings |
| Teacher/Homeroom | Dashboard, Attendance, Classes, Report Cards, Messages |
| Admin Staff | Dashboard, Students, PPDB, Documents, Settings |
| Finance Staff | Dashboard, Payments, SPP, Payroll, Reports |
| Student | Home, Schedule, Grades, Announcements |
| Parent | Home, My Children, Payments, Messages |
| Guest | PPDB Info, Register, Status Check |

Each role's bottom navigation is assembled from the same shared `Destination` set — never a
role-specific duplicate screen. Role determines *which destinations are visible*, not a different
implementation of the same destination.

## Back Stack Rules

1. Each top-level tab keeps its own back stack (standard "tab + stack" pattern) — switching tabs and
   back does not lose place in the previous tab.
2. Deep links (e.g. push notification → specific invoice) construct the correct back stack
   (Dashboard → Payments → PaymentDetail), never a dangling detail screen with no way back to
   context via system back/swipe.
3. Modals (sheets/dialogs) are never part of the destination back stack — they are transient UI
   state, dismissed independently of navigation.

## Transitions

Use the shared transitions defined in `09-ANIMATION.md` (`eduOctoScreenEnter/Exit`) for every
destination change. List → Detail transitions additionally use Shared Element/Shared Transition
where the platform's Compose Multiplatform version supports it (card → detail hero continuity).

## Deep Linking

Each `Destination` that should be deep-linkable (PPDB status, payment detail, announcement) declares
a stable URI pattern (`eduocto://payment/{invoiceId}`) used for push notifications and (later) web.

## Anti-Patterns

- Bottom nav with more than 5 items (collapse into a "More" destination instead).
- Navigating by mutating a global "current screen" enum instead of using the navigation graph.
- Passing large objects (full `Student` model) as navigation arguments — pass an ID, fetch in the
  destination's own ViewModel (see `23-CLEAN-ARCHITECTURE.md`, `24-STATE-MANAGEMENT.md`).
