# 18 — Loading States

## Principle

EduOcto never shows a bare, content-less spinner for anything that takes longer than ~300ms.
Skeletons that mirror the eventual content's shape are the default loading pattern — they
communicate structure and perceived performance, satisfying "where am I / what's coming" even before
data arrives.

## Skeleton Rules

1. Skeleton shapes match the real content's layout exactly (same card sizes, same number of
   placeholder lines as a typical row would have) — never a single generic gray box for a whole
   complex card.
2. Skeletons use a subtle shimmer animation: a soft gradient sweep, `durationSlow` (400ms) per pass,
   looping, using `outlineSubtle` → `outline` → `outlineSubtle` gradient stops.
3. Skeleton count for a list defaults to enough to fill the visible viewport (typically 4–6 rows),
   not an arbitrary fixed number.

```kotlin
@Composable
fun EduOctoSkeleton(modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(EduOctoTheme.shapes.radiusSmall)) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate by transition.animateFloat(
        initialValue = -200f, targetValue = 200f,
        animationSpec = infiniteRepeatable(tween(EduOctoMotion.durationSlow * 3, easing = LinearEasing)),
        label = "shimmerTranslate",
    )
    Box(
        modifier
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        EduOctoTheme.colors.outline.copy(alpha = 0.3f),
                        EduOctoTheme.colors.outline.copy(alpha = 0.6f),
                        EduOctoTheme.colors.outline.copy(alpha = 0.3f),
                    ),
                    start = Offset(translate, 0f), end = Offset(translate + 200f, 0f),
                ),
            ),
    )
}
```

## When to Use What

| Scenario | Pattern |
|---|---|
| Initial screen/section load | Skeleton matching final layout |
| Pagination "load more" | Small inline spinner footer, not a full skeleton replay |
| Button-triggered action (submit, save) | In-button spinner (see `13-FORM-STANDARDS.md`) |
| Full-screen blocking operation (rare — e.g. logout/session switch) | Full-screen `GlassSurface` overlay with spinner + label explaining what's happening, never an unexplained blocker |
| Pull-to-refresh | Platform-standard refresh indicator at top, content stays visible underneath |
| Background sync (no user-blocking needed) | Silent — optionally a subtle top-bar progress hairline |

## Perceived Performance Rules

1. Render whatever data is already available immediately (cache-first), and show a subtle
   "updating" indicator while fresher data loads in the background — never blank-then-populate when
   cached data exists (offline-first principle, see `21-KMP-STANDARDS.md`).
2. Stagger entrance of loaded content using `09-ANIMATION.md` patterns so the skeleton→content
   transition feels intentional, not an abrupt pop.
3. Never show two competing loading indicators simultaneously on the same screen (e.g. a top
   progress bar *and* a centered spinner) — pick the pattern matching the section per the table above.

## Anti-Patterns

- A single full-screen spinner for a dashboard that has independently-loadable sections.
- Skeletons that don't match final content dimensions, causing a layout jump when real data arrives.
- Disabling all interaction on screen during a background refresh that doesn't require it.
