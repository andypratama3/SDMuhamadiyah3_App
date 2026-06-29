# 08 — Glassmorphism (Premium Glass System)

## Purpose

"Premium Glass" is EduOcto's signature elevated-surface treatment. It is used selectively — for
hero cards, sheets, dialogs, and floating navigation — to create depth and layer separation without
heavy drop shadows or harsh borders. It is **not** applied to every surface (see anti-patterns).

## Anatomy of a Glass Surface

```
┌───────────────────────────────┐
│  Outer soft shadow (ambient)  │
│ ┌───────────────────────────┐ │
│ │ Backdrop blur (12–20dp)   │ │
│ │ Glass fill (20–70% white) │ │
│ │ Inner highlight (top edge)│ │
│ │  [content]                │ │
│ └───────────────────────────┘ │
└───────────────────────────────┘
```

| Layer | Spec |
|---|---|
| Backdrop blur | 12dp–20dp (12dp for inline cards, 20dp for full sheets/dialogs) |
| Glass fill opacity | 20%–70% white (or contextual status tint, see `04-COLOR-SYSTEM.md`) |
| Inner highlight | 1px top-edge light stroke, ~12% white, to suggest a glass edge catching light |
| Outer shadow | Soft, large-radius, low-opacity (`alpha 8–12%`, blur radius 24–40dp), no hard edge |
| Border | **None.** Glass surfaces never use a flat 1px solid border — separation comes from blur + shadow, not a stroke |

## Compose Multiplatform Implementation

True backdrop blur support varies by platform in Compose Multiplatform. Use this layered strategy:

```kotlin
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = EduOctoTheme.shapes.radiusLarge,
    tint: Color = EduOctoTheme.colors.surfaceGlass,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = EduOctoTheme.colors.primary.copy(alpha = 0.08f),
                spotColor = EduOctoTheme.colors.primary.copy(alpha = 0.08f),
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(tint)
            // Where the platform supports it (Android 12+/Desktop), apply real blur:
            .then(
                if (supportsBackdropBlur()) Modifier.blurBehind(radius = 16.dp)
                else Modifier // iOS/older Android: rely on translucent fill + shadow only
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.35f), Color.Transparent),
                ),
                shape = RoundedCornerShape(cornerRadius),
            ),
        content = content,
    )
}
```

`supportsBackdropBlur()` is an `expect/actual` capability check (see `21-KMP-STANDARDS.md`) — Android
uses `Modifier.blur`/`RenderEffect` where available, iOS implementations may use `UIVisualEffectView`
interop if a native bridge is later added, Desktop can use a Skia blur. **The fallback (translucent
fill + soft shadow, no blur) must look intentional, not broken** — design glass components so they
hold up without blur, since blur is treated as a progressive enhancement, not a requirement.

## When to Use Glass

✅ Use Premium Glass for:
- Hero/summary cards on dashboards (e.g. "Total SPP Outstanding")
- Bottom sheets and dialogs
- Floating navigation bars / FAB containers
- Onboarding and auth screens (login, PPDB landing)

❌ Do **not** use Premium Glass for:
- Dense data tables/lists (use flat `surface` + `outline` per `06-SPACING-SYSTEM.md` elevation L1)
- Form fields and inputs
- Every card on a busy screen — if everything is glass, nothing reads as elevated

## Accessibility Note

Glass fill opacity must never drop low enough that text contrast fails WCAG AA against the *busiest*
likely background behind it. Test glass components over the actual app background (`Soft White`,
patterned imagery, or a card-rich scroll), not just a flat color, before approving opacity values.
