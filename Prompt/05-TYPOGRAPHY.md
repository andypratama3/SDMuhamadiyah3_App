# 05 — Typography

## Typeface

**Inter** is the only typeface used across EduOcto, on every platform. Bundle it as a Compose
Multiplatform font resource (`composeResources/font/`) rather than relying on system fonts, so
typography is pixel-identical on Android, iOS, and Desktop.

```kotlin
val InterFontFamily = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal),
    Font(Res.font.inter_medium, FontWeight.Medium),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
    Font(Res.font.inter_bold, FontWeight.Bold),
)
```

## Type Scale

A strict, finite scale. **No arbitrary `fontSize` values anywhere in the codebase.**

| Token | Size / Line Height | Weight | Usage |
|---|---|---|---|
| `displayLarge` | 34 / 41 sp | Bold (700) | Hero numbers, auth screen titles |
| `displayMedium` | 28 / 35 sp | SemiBold (600) | Dashboard section heroes (e.g. balance) |
| `titleLarge` | 22 / 28 sp | SemiBold (600) | Screen titles |
| `titleMedium` | 18 / 24 sp | SemiBold (600) | Card titles, dialog titles |
| `bodyLarge` | 16 / 24 sp | Regular (400) | Primary reading content |
| `bodyMedium` | 14 / 20 sp | Regular (400) | Default UI text, list item primary text |
| `bodySmall` | 13 / 18 sp | Regular (400) | Secondary descriptions |
| `labelLarge` | 14 / 20 sp | Medium (500) | Button labels, tabs |
| `labelSmall` | 12 / 16 sp | Medium (500) | Chips, badges |
| `metadata` | 12 / 16 sp | Regular (400), `onSurfaceMuted` | Timestamps, helper text, captions |

## Kotlin Definition

```kotlin
fun eduOctoTypography() = EduOctoTypography(
    displayLarge = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 34.sp, lineHeight = 41.sp, letterSpacing = (-0.25).sp),
    displayMedium = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp, lineHeight = 35.sp, letterSpacing = (-0.2).sp),
    titleLarge = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 13.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    labelSmall = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.2.sp),
    metadata = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.1.sp),
)
```

## Hierarchy Rules

1. **One `displayLarge`/`displayMedium` per screen, maximum.** These are reserved for the single
   most important number or statement on the screen (e.g. total SPP balance, today's attendance %).
2. **Title sizes never skip a level.** A `titleMedium` card never contains a `titleLarge` child.
3. **Metadata is always `onSurfaceMuted`,** never the default ink color — this is what creates the
   "wide metadata spacing" / clear visual rhythm called for in the brand brief.
4. **Numerals use tabular figures** (`FontFeatureSetting("tnum")` where supported) in any place
   numbers are compared vertically (tables, financial lists) so digits align.
5. **Dynamic Type / font scaling must be respected** — see `26-ACCESSIBILITY.md`. Never disable
   system font scaling; layouts must reflow gracefully up to 130% scale.

## Anti-Patterns

- Inline `fontSize = 15.sp` or similar one-off values outside this token set.
- Using `bodyLarge` for paragraphs longer than ~3 lines on mobile — drop to `bodyMedium` for density.
- Centered body text blocks (left-align all reading content; center only short labels/numbers).
