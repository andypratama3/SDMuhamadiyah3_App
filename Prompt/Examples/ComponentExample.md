# Component Example

A worked example of building a new shared component end-to-end — `StatTile`, referenced throughout
`Blueprints/` and `20-COMPONENT-LIBRARY.md` — demonstrating `07-COMPONENT-SYSTEM.md` and
`Checklists/DESIGN_SYSTEM_CHECKLIST.md` applied concretely.

## Why this component exists

`StatTile` is used on every dashboard (`12-DASHBOARD-STANDARDS.md`) and analytics screen
(`ERP/Analytics.md`) to show a labeled value with optional trend and semantic tone. No existing
primitive composes this cleanly, so it earns a place in `20-COMPONENT-LIBRARY.md`.

## Component

```kotlin
enum class StatTone { Neutral, Success, Warning, Danger }

@Composable
fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    tone: StatTone = StatTone.Neutral,
    trend: Trend? = null,
    isLoading: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val toneColor = when (tone) {
        StatTone.Neutral -> EduOctoTheme.colors.onSurface
        StatTone.Success -> EduOctoTheme.colors.success
        StatTone.Warning -> EduOctoTheme.colors.warning
        StatTone.Danger -> EduOctoTheme.colors.danger
    }

    EduOctoCard(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .semantics(mergeDescendants = true) {
                contentDescription = "$label: $value" + (trend?.let { ", tren ${it.direction.name.lowercase()} ${it.percent}%" } ?: "")
            },
    ) {
        Column(Modifier.padding(EduOctoTheme.spacing.md)) {
            if (isLoading) {
                EduOctoSkeleton(Modifier.width(80.dp).height(12.dp))
                Spacer(Modifier.height(EduOctoTheme.spacing.xs))
                EduOctoSkeleton(Modifier.width(64.dp).height(24.dp))
                return@Column
            }
            Text(label, style = EduOctoTheme.typography.bodySmall, color = EduOctoTheme.colors.onSurfaceMuted)
            Spacer(Modifier.height(EduOctoTheme.spacing.xs))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(value, style = EduOctoTheme.typography.titleLarge, color = toneColor)
                trend?.let {
                    Spacer(Modifier.width(EduOctoTheme.spacing.xs))
                    TrendBadge(it)
                }
            }
        }
    }
}

@Composable
private fun TrendBadge(trend: Trend) {
    val (icon, color) = when (trend.direction) {
        TrendDirection.Up -> Icons.Filled.ArrowUpward to EduOctoTheme.colors.success
        TrendDirection.Down -> Icons.Filled.ArrowDownward to EduOctoTheme.colors.danger
        TrendDirection.Flat -> Icons.Filled.ArrowForward to EduOctoTheme.colors.onSurfaceMuted
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        Text("${trend.percent}%", style = EduOctoTheme.typography.labelSmall, color = color)
    }
}
```

## Required States Covered

| State | Covered by |
|---|---|
| Default | `value` rendered directly |
| Loading | `isLoading = true` → skeleton blocks matching label/value shape |
| With trend | `trend` parameter renders `TrendBadge` |
| Clickable | `onClick` makes the whole tile a tap target with merged semantics |
| Tone variants | `StatTone` enum drives the value's color |

## Previews

```kotlin
@Preview @Composable private fun StatTileDefaultPreview() = EduOctoTheme {
    StatTile("Kehadiran Hari Ini", "94%", tone = StatTone.Success, trend = Trend(TrendDirection.Up, 2.1))
}
@Preview @Composable private fun StatTileLoadingPreview() = EduOctoTheme { StatTile("", "", isLoading = true) }
@Preview @Composable private fun StatTileWarningPreview() = EduOctoTheme {
    StatTile("Tunggakan SPP", "Rp4.200.000", tone = StatTone.Warning)
}
@Preview @Composable private fun StatTileLongLabelPreview() = EduOctoTheme {
    StatTile("Tingkat Penagihan SPP Bulan Ini Dibanding Bulan Lalu", "87%", modifier = Modifier.width(160.dp))
}
@Preview @Composable private fun StatTileOnDarkPreview() = EduOctoTheme {
    Box(Modifier.background(EduOctoTheme.colors.primary).padding(16.dp)) {
        StatTile("Total Siswa", "482", tone = StatTone.Neutral)
    }
}
```

## `20-COMPONENT-LIBRARY.md` Entry (added in the same change)

| Component | Required States | Notes |
|---|---|---|
| `StatTile` | default, loading (skeleton) | Label + value + optional trend indicator (▲/▼ + %) |

This example demonstrates the full loop: justify → build → cover all required states → preview
comprehensively → register in the canonical inventory, per `COMPONENT_PROMPT.md`.
