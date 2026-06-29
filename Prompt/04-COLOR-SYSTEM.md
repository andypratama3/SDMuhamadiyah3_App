# 04 — Color System

## Brand Palette

| Token | Hex | Usage |
|---|---|---|
| Deep Navy (Primary) | `#001B3D` | Brand surfaces, primary buttons, headers, nav rail |
| Academic Gold (Secondary) | `#D4AF37` | Highlights, premium accents, key metrics, badges of distinction |
| Modern Blue (Accent) | `#2D6CDF` | Interactive elements, links, active states, info |
| Soft White (Background) | `#F7F8FA` | App background |
| Pure Surface | `#FFFFFF` | Card/sheet surfaces on light background |
| Premium Glass | `rgba(255,255,255,0.55)` over blur | Elevated glass surfaces — see `08-GLASSMORPHISM.md` |

## Semantic Colors

| Token | Hex | Meaning |
|---|---|---|
| Success | `#1E8E5A` | Paid, present, approved, completed |
| Warning | `#C98A1D` | Due soon, pending, partial |
| Danger | `#C13A3A` | Overdue, absent, rejected, destructive action |
| Info | `#2D6CDF` | Neutral informational state |

## Neutrals (text & outline scale)

| Token | Hex | Usage |
|---|---|---|
| `onSurface` (Ink 900) | `#0F1722` | Primary text |
| `onSurfaceMuted` (Ink 600) | `#525B66` | Secondary text, metadata |
| `onSurfaceFaint` (Ink 400) | `#8B93A0` | Placeholder, disabled text |
| `outline` (Ink 200) | `#E2E6EB` | Hairline borders, dividers |
| `outlineSubtle` (Ink 100) | `#EFF1F4` | Subtle separators inside cards |

## Color Roles — Rules

1. **Color always carries meaning.** Green/amber/red are reserved exclusively for
   success/warning/danger semantics (payment status, attendance status, approval status). Never use
   them decoratively.
2. **Navy is the brand anchor, not the dominant fill.** Large flat navy fills are reserved for nav
   chrome, headers, and hero/auth surfaces — not for every card.
3. **Gold is scarce.** Academic Gold is used for emphasis only: a key KPI number, a premium badge, a
   highlighted row. If more than ~10% of a screen is gold, it has been overused.
4. **Contrast is mandatory.** Every text/background pair must meet WCAG AA (4.5:1 for body text,
   3:1 for large text ≥18sp/bold ≥14sp). See `26-ACCESSIBILITY.md` for the verification procedure.
5. **No pure black.** Use `onSurface (#0F1722)` instead of `#000000` for text — pure black against
   Soft White is harsher than the calm/premium target.

## Status Color Mapping (used across every ERP module)

| Status | Color Token | Examples |
|---|---|---|
| `Paid` / `Present` / `Approved` / `Active` | success | Payment list, attendance, PPDB approval |
| `Pending` / `Partial` / `Upcoming` | warning | SPP due in 3 days, leave request pending |
| `Overdue` / `Absent` / `Rejected` / `Inactive` | danger | SPP overdue, unexcused absence |
| `Draft` / `Scheduled` / `Info` | info | Draft announcement, scheduled report card release |

## Glass Tinting

Glass surfaces (see `08-GLASSMORPHISM.md`) are tinted with a low-opacity wash of the *contextual*
semantic color rather than always being neutral white-glass — e.g. a "Payment overdue" glass card
uses a faint danger-tinted glass (`danger` at 6–8% opacity composited under the standard glass blur),
so status is perceivable even at a glance, before reading any text.

## Kotlin Token Mapping

```kotlin
fun eduOctoLightColors() = EduOctoColors(
    primary = Color(0xFF001B3D),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFD4AF37),
    onSecondary = Color(0xFF1A1300),
    background = Color(0xFFF7F8FA),
    surface = Color(0xFFFFFFFF),
    surfaceGlass = Color(0x8CFFFFFF), // 55% white, composited with blur modifier
    onSurface = Color(0xFF0F1722),
    onSurfaceMuted = Color(0xFF525B66),
    outline = Color(0xFFE2E6EB),
    success = Color(0xFF1E8E5A),
    warning = Color(0xFFC98A1D),
    danger = Color(0xFFC13A3A),
    info = Color(0xFF2D6CDF),
)
```

## Dark Mode (forward-looking — not required for v1, but tokens must support it)

Dark mode is not in the initial scope, but the token *architecture* (Layer 1/2/3 in
`03-DESIGN-SYSTEM.md`) must allow an `eduOctoDarkColors()` to be added later without touching any
component code. Do not build components that read raw light-mode hex values directly.
