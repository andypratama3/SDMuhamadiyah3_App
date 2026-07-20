# SDM3 UI Design Guidelines

## Design Philosophy

SDM3 follows a **Premium Educational App** design philosophy with the following principles:

1. **Glassmorphism** - Frosted glass effects with subtle transparency
2. **Soft Structuralism** - Clean, organized layouts with clear hierarchy
3. **Premium Animations** - Smooth, purposeful animations with premium easing curves
4. **Consistent Branding** - School colors with modern Material 3

## Color System

### Primary Colors
- **Primary**: `MaterialTheme.colorScheme.primary` - Deep blue, used for headings, icons
- **Secondary**: `MaterialTheme.colorScheme.secondary` - Accent green, used for CTAs
- **Tertiary**: `MaterialTheme.colorScheme.tertiary` - Accent orange, used for warnings

### Status Colors
```kotlin
statusSuccessColor() // Green - success states, "Hadir"
statusWarningColor() // Orange - warning states, "Sakit"  
statusDangerColor()  // Red - error states, "Alpa"
statusInfoColor()    // Blue - info states
```

### Glass Colors
```kotlin
glassSurfaceColor()   // Semi-transparent surface
glassBorderColor()    // Subtle glass border
heroContentColor()    // Content on primary background
overlayScrimColor()   // Dark overlay for modals
```

## Typography

### Hierarchy
1. **Display** - Large titles (numbers, amounts)
2. **Headline** - Section titles
3. **Title** - Card titles, item titles
4. **Body** - Main content
5. **Label** - Labels, badges, small text

### Font Weights
- **Black** (900): Badges, uppercase labels
- **Bold** (700): Titles, important text
- **SemiBold** (600): Emphasized body text
- **Medium** (500): Regular body text

## Spacing

Use `Spacing` object or `ProductSchoolTheme.spacing`:

```kotlin
Spacing.xs  // 4.dp
Spacing.sm  // 8.dp
Spacing.md  // 16.dp
Spacing.lg  // 24.dp
Spacing.xl  // 32.dp
Spacing.xxl // 48.dp
```

## Component Patterns

### Cards
```kotlin
// Basic card
Sdm3Card(padding = 16.dp) { /* content */ }

// Glassmorphic card
Sdm3GlassCard(padding = 20.dp) { /* content */ }

// Clickable card
Sdm3Card(onClick = { /* action */ }) { /* content */ }
```

### Buttons
```kotlin
// Primary button
Sdm3Button(
    text = "Submit",
    onClick = { /* action */ },
    isLoading = false
)

// Outlined button
Sdm3OutlinedButton(
    text = "Cancel",
    onClick = { /* action */ }
)
```

### Text Fields
```kotlin
Sdm3TextField(
    value = text,
    onValueChange = { },
    label = "Email",
    leadingIcon = Icons.Outlined.Email,
    isError = false
)
```

## Animation Curves

### Premium Easing
```kotlin
val PremiumEasing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)
val FastOutSlowInEasing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
```

### Animation Durations
- **Fast**: 100-200ms - Press effects, small UI changes
- **Normal**: 300-500ms - Standard transitions
- **Slow**: 600-1000ms - Hero animations, page transitions

## Shimmer Loading

Always use shimmer for loading states:

```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
        .clip(RoundedCornerShape(16.dp))
        .shimmerEffect()
)
```

## Atmospheric Background

Use atmospheric glows for visual interest:

```kotlin
Canvas(modifier = Modifier.fillMaxSize().alpha(0.4f)) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(colorScheme.primary.copy(alpha = 0.15f), Color.Transparent),
            center = Offset(size.width * 0.85f, size.height * 0.1f),
            radius = size.width * 1.5f
        )
    )
}
```

## Empty & Error States

```kotlin
// Empty state
Sdm3EmptyState(
    title = "Belum Ada Data",
    message = "Data belum tersedia.",
    style = EmptyStateStyle.Neutral,
    action = { Sdm3Button(text = "Refresh", onClick = {}) }
)

// Error state
Sdm3ErrorState(
    title = "Gagal Memuat",
    message = "Silakan coba kembali.",
    style = ErrorStateStyle.Network,
    primaryAction = { Sdm3Button(text = "Coba Lagi", onClick = {}) }
)
```

## Haptic Feedback

Always include haptic feedback for interactive elements:

```kotlin
val haptic = LocalHapticFeedback.current
// On button press
haptic.performHapticFeedback(HapticFeedbackType.LongPress)
// On light interaction
haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
```

## Formatters

Use centralized formatters for consistency:

```kotlin
formatRupiah(350000) // "Rp350.000"
nameInitials("Ahmad Fauzi") // "AF"
formatTanggal("2026-06-13") // "13 Jun 2026"
formatClassName("1 Baghdad") // "Kelas 1 Baghdad"
```

## Accessibility

- All icons should have content descriptions
- Touch targets minimum 48.dp
- Color contrast ratio minimum 4.5:1
- Support reduced motion preferences

## File Naming Convention

- Components: `Sdm3*.kt` (e.g., `Sdm3Button.kt`)
- Screens: `*Screen.kt` (e.g., `LoginScreen.kt`)
- ViewModels: `*ViewModel.kt`
- Theme: `Theme.kt`, `Color.kt`, `Typography.kt`

## Best Practices

1. **Always use design system components** - Don't create ad-hoc components
2. **Consistent spacing** - Use Spacing constants, not magic numbers
3. **Preview everything** - Add @Preview composables for visual testing
4. **Dark mode support** - Test all screens in both themes
5. **Loading states** - Always show shimmer during data fetching
6. **Error handling** - Graceful error states with retry actions
