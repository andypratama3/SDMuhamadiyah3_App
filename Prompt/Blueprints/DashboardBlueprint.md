# Dashboard Blueprint

Concrete screen blueprint implementing `12-DASHBOARD-STANDARDS.md` for any role. Use this as the
structural template; fill role-specific content per `ERP/Dashboard.md`'s aggregation table.

## Composable Structure

```
DashboardScreen
└── DashboardContent(state: DashboardUiState)
    ├── EduOctoTopBar(greeting, roleBadge, dateLabel)
    ├── HeroStatCard            (GlassSurface, displayMedium value, trend indicator)
    ├── SecondaryStatsRow       (2–4 StatTile, horizontally arranged or wrapped on compact)
    ├── ActionableItemsSection  (EduOctoSection, list of ActionableItemRow, or EmptyState if none)
    └── RecentActivitySection   (EduOctoSection, list of ActivityRow, or EmptyState if none)
```

## State Shape

```kotlin
data class DashboardUiState(
    val isLoading: Boolean = false,
    val hero: HeroStat? = null,
    val secondaryStats: ImmutableList<SecondaryStat> = persistentListOf(),
    val actionableItems: ImmutableList<ActionableItem> = persistentListOf(),
    val recentActivity: ImmutableList<ActivityEvent> = persistentListOf(),
    val sectionErrors: ImmutableMap<DashboardSection, AppError> = persistentMapOf(),
)
enum class DashboardSection { Hero, SecondaryStats, ActionableItems, RecentActivity }
```

Each section tracks its own loading/error independently (per `12-DASHBOARD-STANDARDS.md` rule that
a slow section must not block others) — represented here as a per-section error map rather than one
global error flag.

## Layout by Window Size

- **Compact:** single column, sections stacked vertically in the order above.
- **Medium:** `SecondaryStatsRow` becomes a 2x2 grid; `ActionableItems`/`RecentActivity` remain
  stacked.
- **Expanded:** two-column layout — Hero + SecondaryStats in a left column (≈60% width),
  ActionableItems + RecentActivity in a right column (≈40% width).

## Required States

| Section | Loading | Empty | Error |
|---|---|---|---|
| Hero | Skeleton matching hero card shape | N/A (hero always has a value or shows "—") | Inline error chip on the card, retry icon |
| Secondary Stats | 2–4 skeleton tiles | N/A | Per-tile error icon, no full-section failure |
| Actionable Items | 3 skeleton rows | Positive empty state ("Semua beres ✓") | Inline retry banner |
| Recent Activity | 3 skeleton rows | Neutral empty state ("Belum ada aktivitas") | Inline retry banner |

## Permission Notes

Which `ActionableItem`s and which `HeroStat` definition apply is resolved entirely in the
ViewModel/UseCase layer based on the current `UserRole` (`ERP/Settings.md`) — the Composable layer
is role-agnostic; it just renders whatever `DashboardUiState` it's given.
