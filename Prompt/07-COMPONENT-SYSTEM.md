# 07 — Component System

## Component Anatomy

Every EduOcto component follows the same internal structure, regardless of complexity:

```
Component
├── Container       (surface, glass, elevation, shape — from tokens)
├── Leading slot     (icon/avatar/status — optional)
├── Content slot     (title + supporting text, or custom content)
├── Trailing slot    (action/value/chevron — optional)
└── State variants    (default, hover/pressed, focused, disabled, loading, error)
```

## Composition Rules

1. **Slot-based API, not prop explosion.** Components accept `@Composable` slot lambdas
   (`leading: @Composable (() -> Unit)? = null`) rather than dozens of boolean flags.

```kotlin
@Composable
fun EduOctoListItem(
    title: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = EduOctoTheme.spacing.md, vertical = EduOctoTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leading?.let { it(); Spacer(Modifier.width(EduOctoTheme.spacing.sm)) }
        Column(Modifier.weight(1f)) {
            Text(title, style = EduOctoTheme.typography.bodyMedium, color = EduOctoTheme.colors.onSurface)
            supportingText?.let {
                Text(it, style = EduOctoTheme.typography.metadata, color = EduOctoTheme.colors.onSurfaceMuted)
            }
        }
        trailing?.let { Spacer(Modifier.width(EduOctoTheme.spacing.sm)); it() }
    }
}
```

2. **State is explicit and exhaustive.** Any component that loads data exposes a sealed `UiState`
   with `Loading`, `Empty`, `Error`, `Success` — see `24-STATE-MANAGEMENT.md`. A component must never
   silently render nothing because a case wasn't handled.
3. **No component reaches into a ViewModel/Repository directly.** Components are pure functions of
   their parameters. Screens own data fetching and pass state down (state hoisting).
4. **Every interactive component has a disabled and a loading variant** defined up front, not bolted
   on later.
5. **Components are previewable in isolation** — add `@Preview` functions covering at minimum:
   default, long-text overflow, RTL-safe layout, and dark-background context (for forward dark-mode
   compatibility) for every new component.

## Component Categories

| Category | Examples |
|---|---|
| Primitives | Button, IconButton, TextField, Checkbox, Switch, Chip, Badge, Avatar |
| Layout | Card, GlassCard, Section, ListItem, Divider, ScreenScaffold |
| Data Display | DataTable, StatTile, ProgressRing, Sparkline, StatusPill |
| Feedback | Snackbar, Toast, Dialog, BottomSheet, EmptyState, ErrorState, Skeleton |
| Navigation | TopBar, NavRail, BottomNavBar, TabRow, Breadcrumb |
| Domain-specific | StudentCard, AttendanceRow, PaymentRow, ReportCardGrade, AnnouncementCard |

See `20-COMPONENT-LIBRARY.md` for the canonical, exhaustive inventory with required states for each.

## Naming Convention

All shared components are prefixed `EduOcto` (`EduOctoButton`, `EduOctoCard`) to avoid collision
with Material 3 primitives they wrap, and to make it unambiguous in code review that a design-system
component — not a raw Material widget — is being used.

## Anti-Patterns

- A screen building inline `Row`/`Column` layouts that duplicate an existing component's anatomy
  instead of reusing/extending it.
- Components that accept a `Modifier` but ignore it (every component must accept and apply
  `modifier: Modifier = Modifier` as the outermost modifier).
- "God components" that branch internally on user role — role-specific behavior belongs in the
  screen/ViewModel, components stay role-agnostic and reusable.
