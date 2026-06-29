# 09 — Animation & Motion System

## Principle

Nothing appears instantly. Every state change a user causes or perceives is communicated through
motion — but motion is always purposeful (clarifying cause → effect), never decorative.

## The EduOcto Easing Curve

A single custom cubic-bezier easing is used as the default across the entire app, to give EduOcto a
consistent, recognizable "feel" distinct from default Material motion:

```kotlin
val EduOctoEasing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)
```

This curve has a brisk start and a soft, natural deceleration — it reads as confident, not bouncy.

## Duration Scale

| Token | Duration | Usage |
|---|---|---|
| `durationInstant` | 80ms | Micro feedback (press state, ripple) |
| `durationFast` | 150ms | Small UI transitions (toggle, chip select) |
| `durationDefault` | 250ms | Standard transitions (card expand, fade-in content) |
| `durationSlow` | 400ms | Screen transitions, sheet/dialog entry |
| `durationStagger` | 40ms per item | Stagger delay between list items on entry |

```kotlin
object EduOctoMotion {
    val easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)
    const val durationInstant = 80
    const val durationFast = 150
    const val durationDefault = 250
    const val durationSlow = 400
    const val staggerStep = 40
}
```

## Animation Types & When to Use Them

| Type | Use Case |
|---|---|
| **Fade** | Content swaps where position doesn't change (tab content, loading→loaded) |
| **Slide** | Navigation between screens (horizontal), bottom sheets (vertical from bottom) |
| **Blur Reveal** | Hero content entering on dashboards/auth — pairs with `08-GLASSMORPHISM.md` |
| **Scale** | Buttons on press (scale to 0.96), dialogs entering (scale from 0.95 → 1.0 + fade) |
| **Shared Transition** | List item → detail screen (e.g. student card → student detail) |
| **Stagger** | Lists/grids of cards appearing on first load (KPI tiles, dashboard sections) |
| **Spring** | Drag-to-dismiss sheets, swipe-to-action list rows — physical, interruptible motion |

## Reference Implementation

```kotlin
// Standard fade+slide screen transition
fun eduOctoScreenEnter(): EnterTransition =
    fadeIn(animationSpec = tween(EduOctoMotion.durationSlow, easing = EduOctoMotion.easing)) +
    slideInHorizontally(
        animationSpec = tween(EduOctoMotion.durationSlow, easing = EduOctoMotion.easing),
        initialOffsetX = { it / 8 },
    )

// Staggered list entry
@Composable
fun <T> StaggeredColumn(items: List<T>, content: @Composable (T) -> Unit) {
    items.forEachIndexed { index, item ->
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(index * EduOctoMotion.staggerStep.toLong())
            visible = true
        }
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(EduOctoMotion.durationDefault, easing = EduOctoMotion.easing)) +
                slideInVertically(tween(EduOctoMotion.durationDefault, easing = EduOctoMotion.easing)) { it / 4 },
        ) { content(item) }
    }
}

// Press scale feedback
@Composable
fun Modifier.eduOctoPressScale(pressed: Boolean): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh),
        label = "pressScale",
    )
    return this.graphicsLayer { scaleX = scale; scaleY = scale }
}
```

## Rules

1. **Every list/grid that loads data animates its entry** (stagger), unless it's a real-time feed
   where reflow would be distracting (e.g. live chat) — those use fade-only for new items.
2. **Every navigation transition uses the shared `eduOctoScreenEnter`/`Exit` pair** — no screen
   defines its own one-off transition.
3. **Respect "reduce motion."** Check the platform's reduced-motion accessibility setting; when
   enabled, replace slide/scale/stagger with simple cross-fades at `durationFast`. Never disable
   transitions entirely — state changes must still be perceivable (see `02-DESIGN-PHILOSOPHY.md`,
   "what changed?").
4. **Animations never block interaction longer than `durationSlow` (400ms).** If a transition would
   take longer (e.g. a large data load), that's a loading state (`18-LOADING-STATES.md`), not an
   animation problem.
5. **No animation loops indefinitely** except an explicit loading indicator. Decorative idle
   animation (e.g. a pulsing icon "for delight") is disallowed — it adds noise without communicating
   the five key questions in `02-DESIGN-PHILOSOPHY.md`.
