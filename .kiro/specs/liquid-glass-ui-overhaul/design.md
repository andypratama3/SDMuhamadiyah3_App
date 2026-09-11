# Design Document: Liquid Glass UI Overhaul

## Architecture Overview

The Liquid Glass design system extends the existing `ProductSchoolTheme` architecture without breaking backward compatibility. All changes live in `commonMain` and layer on top of the current token hierarchy.

### Token Hierarchy

```
SDM3Theme (wrapper)
└── ProductSchoolTheme (CompositionLocalProvider)
    ├── LocalProductSchoolColors → ProductSchoolColors (extended with Liquid Glass tokens)
    ├── LocalProductSchoolTypography → ProductSchoolTypography
    ├── LocalProductSchoolSpacing → ProductSchoolSpacing
    ├── LocalProductSchoolShapes → ProductSchoolShapes
    └── LocalProductSchoolElevation → ProductSchoolElevation
        │
        ├── Spacing object (extended with bottomNavSafeArea)
        │
        └── MaterialTheme (M3 color scheme derived from ProductSchoolColors)
```

### Token Flow

```
ProductSchoolColors (data class)
├── liquidGlassSurface    ← new
├── liquidGlassBorder     ← new
├── liquidGlassHighlight  ← new
├── liquidGlassShadow     ← new
├── successContainer      ← new
├── warningContainer      ← new
├── dangerContainer       ← new
├── infoContainer         ← new
├── surfaceGlass          ← existing (kept for backward compat)
└── glassOutline          ← existing (kept for backward compat)
```

Components consume tokens exclusively through `ProductSchoolTheme.colors.*` — never hardcoded hex values.

---

## New Tokens

### Color Tokens (ProductSchoolColors additions)

```kotlin
// In ProductSchoolColors data class — NEW fields:
val liquidGlassSurface: Color,
val liquidGlassBorder: Color,
val liquidGlassHighlight: Color,
val liquidGlassShadow: Color,
val successContainer: Color,
val warningContainer: Color,
val dangerContainer: Color,
val infoContainer: Color,
```

#### Light Mode Values

| Token                  | Value                         | Alpha | Notes                           |
|------------------------|-------------------------------|-------|---------------------------------|
| `liquidGlassSurface`  | `Color(0xD9FFFFFF)`           | 0.85  | Primary glass fill              |
| `liquidGlassBorder`   | `Color(0x4DFFFFFF)`           | 0.30  | Gradient border top edge        |
| `liquidGlassHighlight`| `Color(0x59FFFFFF)`           | 0.35  | Inner highlight for depth       |
| `liquidGlassShadow`   | `Color(0x14000000)`           | 0.08  | Subtle drop shadow color        |
| `successContainer`    | `Color(0x1A1E8E5A)`           | 0.10  | Badge bg for success status     |
| `warningContainer`    | `Color(0x1AC98A1D)`           | 0.10  | Badge bg for warning status     |
| `dangerContainer`     | `Color(0x1AC13A3A)`           | 0.10  | Badge bg for danger status      |
| `infoContainer`       | `Color(0x1A2D6CDF)`           | 0.10  | Badge bg for info status        |

#### Dark Mode Values

| Token                  | Value                         | Alpha | Notes                              |
|------------------------|-------------------------------|-------|------------------------------------|
| `liquidGlassSurface`  | `Color(0xD91E293B)`           | 0.85  | Dark glass fill, elevated feel     |
| `liquidGlassBorder`   | `Color(0x26FFFFFF)`           | 0.15  | Subtle white edge in dark          |
| `liquidGlassHighlight`| `Color(0x1AFFFFFF)`           | 0.10  | Reduced highlight in dark          |
| `liquidGlassShadow`   | `Color(0x33000000)`           | 0.20  | Stronger shadow in dark            |
| `successContainer`    | `Color(0x264ADE80)`           | 0.15  | Badge bg for success status        |
| `warningContainer`    | `Color(0x26FBBF24)`           | 0.15  | Badge bg for warning status        |
| `dangerContainer`     | `Color(0x26F87171)`           | 0.15  | Badge bg for danger status         |
| `infoContainer`       | `Color(0x2660A5FA)`           | 0.15  | Badge bg for info status           |

### Spacing Tokens (Spacing object additions)

```kotlin
object Spacing {
    // ... existing tokens unchanged ...
    val bottomNavSafeArea = 104.dp  // navBar height (72.dp) + vertical padding (16.dp * 2)
}
```

### ThemeExt.kt additions

```kotlin
/** Liquid Glass surface fill */
@Composable
fun liquidGlassSurfaceColor(): Color = ProductSchoolTheme.colors.liquidGlassSurface

/** Liquid Glass border color */
@Composable
fun liquidGlassBorderColor(): Color = ProductSchoolTheme.colors.liquidGlassBorder

/** Liquid Glass inner highlight */
@Composable
fun liquidGlassHighlightColor(): Color = ProductSchoolTheme.colors.liquidGlassHighlight

/** Liquid Glass shadow */
@Composable
fun liquidGlassShadowColor(): Color = ProductSchoolTheme.colors.liquidGlassShadow
```

---

## Component Changes

### 1. Sdm3GlassCard — Unified Liquid Glass Card

The existing `Sdm3GlassCard` becomes the canonical glass component. It already uses `ProductSchoolTheme.colors.surfaceGlass` and `glassOutline`. The migration:

```kotlin
@Composable
fun Sdm3GlassCard(
    modifier: Modifier = Modifier,
    padding: Dp = 0.dp,
    content: @Composable () -> Unit,
) {
    val colors = ProductSchoolTheme.colors
    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                // Hardware-accelerated transparency
                shadowElevation = 4f
                shape = CardShape
                clip = true
                ambientShadowColor = colors.liquidGlassShadow
                spotShadowColor = colors.liquidGlassShadow
            },
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = colors.liquidGlassSurface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    colors.liquidGlassBorder,
                    Color.Transparent,
                ),
            ),
        ),
    ) {
        Column(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}
```

**Key changes:**
- Replace `surfaceGlass` → `liquidGlassSurface`
- Replace `glassOutline` → `liquidGlassBorder`
- Use `graphicsLayer` for hardware-accelerated shadow
- Remove explicit elevation from CardDefaults (shadow via graphicsLayer)

### 2. Sdm3Card — Adopt Glass Tokens

`Sdm3Card` becomes glass-aware while keeping its API stable:

```kotlin
@Composable
fun Sdm3Card(
    modifier: Modifier = Modifier,
    padding: Dp = 0.dp,
    border: BorderStroke? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val colors = ProductSchoolTheme.colors
    val cardBorder = border ?: BorderStroke(
        width = 0.5.dp,
        color = colors.liquidGlassBorder,
    )

    val cardModifier = modifier
        .fillMaxWidth()
        .graphicsLayer {
            shadowElevation = 2f
            shape = CardShape
            clip = true
            ambientShadowColor = colors.liquidGlassShadow
            spotShadowColor = colors.liquidGlassShadow
        }

    // ... rest of Card implementation using cardModifier, containerColor = colors.liquidGlassSurface
}
```

### 3. SDM3BottomNavBar — Liquid Glass Surface

Replace `glassSurfaceColor()` / `glassBorderColor()` with new tokens:

```kotlin
@Composable
fun SDM3BottomNavBar(...) {
    val colors = ProductSchoolTheme.colors
    // Replace:
    //   val glassSurface = glassSurfaceColor()
    //   val glassBorder = glassBorderColor()
    // With:
    val glassSurface = colors.liquidGlassSurface
    val glassBorder = colors.liquidGlassBorder

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .graphicsLayer {
                shadowElevation = 24f
                shape = RoundedCornerShape(36.dp)
                clip = true
                ambientShadowColor = colors.liquidGlassShadow
                spotShadowColor = colors.liquidGlassShadow
            },
        color = glassSurface,
        shape = RoundedCornerShape(36.dp),
        border = BorderStroke(width = 1.dp, color = glassBorder)
    ) { /* ... */ }
}
```

### 4. Badge — Fix Clipping, Use Container Tokens

```kotlin
@Composable
fun InlineStatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    isSuccess: Boolean = false,
    isWarning: Boolean = false,
    isError: Boolean = false
) {
    val colors = ProductSchoolTheme.colors
    val bgColor = when {
        isError -> colors.dangerContainer
        isWarning -> colors.warningContainer
        isSuccess -> colors.successContainer
        else -> colors.infoContainer
    }
    val textColor = when {
        isError -> colors.danger
        isWarning -> colors.warning
        isSuccess -> colors.success
        else -> colors.info
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 24.dp)  // 48dp touch target
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = textColor,
            fontSize = 10.sp,                   // Minimum 10sp per req 7.3
            letterSpacing = 0.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Visible,    // Prevent clipping
        )
    }
}
```

### 5. Shimmer — Unified Direction & Tokens

```kotlin
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -200f,
        targetValue = 1000f,       // Left-to-right single direction
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart    // Consistent direction (no reverse)
        ),
        label = "shimmerTranslate"
    )

    val colorScheme = MaterialTheme.colorScheme
    val baseColor = colorScheme.surfaceVariant.copy(alpha = 0.6f)
    val highlightColor = colorScheme.onSurface.copy(alpha = 0.1f)

    background(
        brush = Brush.linearGradient(
            colors = listOf(baseColor, highlightColor, baseColor),
            start = Offset(x = translateAnim, y = 0f),
            end = Offset(x = translateAnim + 200f, y = 0f)   // Horizontal only
        )
    )
}
```

### 6. AtmosphericGlow — graphicsLayer + Dark Mode Alpha Cap

```kotlin
@Composable
fun AtmosphericGlow(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopEnd,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    alpha: Float = if (isSystemInDarkTheme()) 0.10f else 0.15f  // Dark mode cap: 0.15 max
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { clip = false }   // Hardware layer
            .zIndex(-1f),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(fraction = 0.8f)
                .graphicsLayer { this.alpha = alpha }
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(color, Color.Transparent)
                    )
                )
        )
    }
}
```

---

## Press State Animation Pattern

A reusable `Modifier.pressEffect()` extension for interactive elements:

```kotlin
@Composable
fun Modifier.pressEffect(
    interactionSource: MutableInteractionSource
): Modifier = composed {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(
            durationMillis = Sdm3Motion.durationFast,
            easing = Sdm3Motion.easing
        ),
        label = "pressScale"
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
```

---

## Screen-Level Integration Pattern

Each tab screen follows this pattern:

```kotlin
@Composable
fun XxxScreen(...) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Atmospheric background (behind content)
        AtmosphericGlow(
            alignment = Alignment.TopEnd,
            color = MaterialTheme.colorScheme.primaryContainer,
            // alpha auto-adjusts for dark mode
        )

        // 2. Scrollable content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Spacing.lg,           // 20.dp horizontal
                end = Spacing.lg,
                top = Spacing.md,
                bottom = Spacing.bottomNavSafeArea  // Safe area for floating nav
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // 3. Use design system components exclusively
            item { Sdm3GlassCard(padding = Spacing.md) { /* hero content */ } }
            items(data) { item ->
                Sdm3GlassCard(padding = Spacing.md) { /* item content */ }
            }
        }
    }
}
```

**Rules for all screens:**
- `Spacing.lg` (20.dp) for horizontal content padding
- `Spacing.bottomNavSafeArea` for bottom padding on tab screens
- `AtmosphericGlow` as first child of the root Box
- All cards use `Sdm3GlassCard` or `Sdm3Card` — no ad-hoc `Surface`
- All icons include `contentDescription` parameter
- All clickable items have minimum 48.dp touch target

---

## File Changes Matrix

| File                              | Change Type    | Description                                                    |
|-----------------------------------|----------------|----------------------------------------------------------------|
| `theme/Color.kt`                  | MODIFY         | Add 8 new fields to `ProductSchoolColors`, update both factory functions |
| `theme/Spacing.kt`                | MODIFY         | Add `bottomNavSafeArea` to `Spacing` object                   |
| `theme/ThemeExt.kt`               | MODIFY         | Add 4 new liquid glass helper functions                        |
| `component/Sdm3GlassCard.kt`      | MODIFY         | Migrate to new tokens, add `graphicsLayer`                     |
| `component/Sdm3Card.kt`           | MODIFY         | Add glass border + shadow via `graphicsLayer`                  |
| `component/SDM3BottomNavBar.kt`   | MODIFY         | Use new tokens, existing `graphicsLayer` already present       |
| `component/Badge.kt`              | MODIFY         | Use container tokens, fix clipping, enforce 10sp min           |
| `component/Shimmer.kt`            | MODIFY         | Fix direction (Restart), horizontal-only gradient              |
| `component/AtmosphericGlow.kt`    | MODIFY         | Add `graphicsLayer`, dark mode alpha cap                       |
| `component/Modifiers.kt`          | MODIFY         | Add `pressEffect()` modifier extension                        |
| `feature/home/ui/HomeScreen.kt`   | MODIFY         | Adopt glass cards, AtmosphericGlow, bottomNavSafeArea          |
| `feature/nilai/ui/*Screen.kt`     | MODIFY         | Adopt glass cards, AtmosphericGlow                             |
| `feature/pembayaran/ui/*Screen.kt`| MODIFY         | Adopt glass cards                                              |
| `feature/rapor/ui/*Screen.kt`     | MODIFY         | Adopt glass cards                                              |
| `feature/profil/ui/*Screen.kt`    | MODIFY         | Adopt glass cards                                              |

---

## Backward Compatibility Strategy

1. **Existing `surfaceGlass` and `glassOutline` fields remain** — they are not removed from `ProductSchoolColors`. Old code referencing them still compiles.
2. **Top-level `GlassSurface` and `GlassOutline` vals remain** in Color.kt for any legacy imports.
3. **`glassSurfaceColor()` and `glassBorderColor()` helpers in ThemeExt.kt remain** — they continue to return the existing values. New code should prefer `liquidGlassSurfaceColor()`.
4. **Existing component APIs unchanged** — `Sdm3Card` and `Sdm3GlassCard` keep the same function signatures. Internal implementation changes only.
5. **New fields added with default values** in the data class constructor so any call site constructing `ProductSchoolColors` without the new params still compiles.

---

## Error Handling

- If `liquidGlassSurface` is not provided (default), fall back to existing `surfaceGlass` value.
- `AtmosphericGlow` alpha parameter is clamped: `alpha.coerceIn(0f, 0.15f)` in dark mode.
- Badge `fontSize` enforced at minimum 10.sp via `maxOf(fontSize, 10.sp)`.

---

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system — essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Light mode glass alpha range

*For any* glass surface color token in the light mode palette (`liquidGlassSurface`, `surfaceGlass`), the alpha component SHALL be between 0.55 and 0.85 (inclusive).

**Validates: Requirements 1.2**

### Property 2: Dark mode glass alpha range

*For any* glass surface color token in the dark mode palette (`liquidGlassSurface`, `surfaceGlass`), the alpha component SHALL be between 0.70 and 0.90 (inclusive).

**Validates: Requirements 1.3, 6.1**

### Property 3: Text-on-glass contrast ratio

*For any* combination of text color (`onSurface`, `onSurfaceMuted`) and glass surface background color (`liquidGlassSurface`) in both light and dark palettes, the computed WCAG contrast ratio SHALL be at least 4.5:1.

**Validates: Requirements 1.4, 6.3**

### Property 4: Dark mode atmospheric glow alpha cap

*For any* invocation of `AtmosphericGlow` while in dark mode, the effective alpha applied to the glow SHALL be at most 0.15.

**Validates: Requirements 6.2**

