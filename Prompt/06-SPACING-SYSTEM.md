# 06 — Spacing System

## The 8dp Grid

Every margin, padding, and gap in EduOcto is a multiple of **8dp**, with **4dp** allowed only as a
half-step for tight inline contexts (icon-to-label gaps, chip padding). There is no other exception.

| Token | Value | Typical Use |
|---|---|---|
| `xs` | 4dp | Icon–label gap, chip internal padding |
| `sm` | 8dp | Tight stack spacing, small component padding |
| `md` | 16dp | Default content padding, standard gap between elements |
| `lg` | 24dp | Section spacing, card internal padding |
| `xl` | 32dp | Major section breaks, screen top padding on tablet/desktop |
| `xxl` | 48dp | Hero spacing, empty-state vertical centering offset |

```kotlin
data class EduOctoSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
)
```

## Corner Radius Scale

| Token | Value | Usage |
|---|---|---|
| `radiusSmall` | 8dp | Chips, small buttons, input fields |
| `radiusMedium` | 16dp | Cards, dialogs, primary buttons — **the EduOcto default** |
| `radiusLarge` | 24dp | Sheets, hero cards, glass panels |
| `radiusPill` | 999dp | Pills/badges, segmented controls, FAB |

16dp is the signature radius — when in doubt, use `radiusMedium`.

## Layout Rules

1. **Screen margins:** 16dp on mobile, 24dp on tablet, 32dp on desktop — defined once in a
   `WindowSizeClass`-aware `LocalContentPadding`, never hardcoded per screen.
2. **Card internal padding:** 16dp minimum, 24dp for hero/primary cards.
3. **Vertical rhythm between sections:** 24dp (`lg`) minimum; 32dp (`xl`) between unrelated section
   groups.
4. **Touch targets:** minimum 48x48dp regardless of visual size (use `Modifier.minimumInteractiveComponentSize()`
   or explicit padding to reach 48dp even when the icon itself is 24dp).
5. **Golden ratio (1:1.618)** is applied *only* where it naturally clarifies hierarchy — e.g. a
   two-pane master/detail layout on tablet/desktop may split panes at roughly 38/62 — never forced
   onto component sizing where an 8dp-grid value already works.

## Elevation / Z-Order (paired with `08-GLASSMORPHISM.md`)

| Level | Elevation | Usage |
|---|---|---|
| 0 | flat | Page background |
| 1 | 1dp + hairline outline | Default cards, list items |
| 2 | 4dp shadow | Raised cards (e.g. selected, draggable) |
| 3 | 8dp shadow + glass | Sheets, popovers, dropdown menus |
| 4 | 16dp shadow + glass | Dialogs, modal surfaces |

## Responsive Breakpoints

| Class | Width | Layout behavior |
|---|---|---|
| Compact | < 600dp | Single column, bottom nav |
| Medium | 600–839dp | Two-pane where applicable, nav rail |
| Expanded | ≥ 840dp | Multi-pane, persistent nav rail/drawer, max content width 1200dp centered |

Use Compose Multiplatform's `WindowSizeClass` (or an equivalent KMP-safe calculation) — never branch
on raw pixel widths.

## Anti-Patterns

- Any literal `Dp` value not divisible by 4 (e.g. `13.dp`, `21.dp`).
- Inconsistent radius within the same component family (e.g. one card at 12dp, another at 16dp).
- Edge-to-edge content with no screen margin on mobile.
