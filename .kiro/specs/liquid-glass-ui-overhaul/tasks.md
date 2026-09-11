# Implementation Plan: Liquid Glass UI Overhaul

## Overview

Migrate the SDM3 Parent App design system to use unified Liquid Glass tokens across all components and screens. The implementation starts with foundational token changes (color, spacing, theme extensions), then migrates core components, and finally updates each feature screen to use the new design system consistently.

## Tasks

- [x] 1. Extend Color Tokens
  - [x] 1.1 Add 8 new fields to ProductSchoolColors data class
    - Add `liquidGlassSurface`, `liquidGlassBorder`, `liquidGlassHighlight`, `liquidGlassShadow`, `successContainer`, `warningContainer`, `dangerContainer`, `infoContainer` with default values
    - Update `productSchoolLightColors()` with light mode values as specified in design
    - Update `productSchoolDarkColors()` with dark mode values as specified in design
    - File: `shared/src/commonMain/kotlin/com/sdm3/parent/core/designsystem/theme/Color.kt`
    - _Requirements: 1.1, 1.2, 1.3, 1.5_

  - [ ]* 1.2 Write property tests for glass alpha ranges
    - **Property 1: Light mode glass alpha range**
    - **Property 2: Dark mode glass alpha range**
    - **Validates: Requirements 1.2, 1.3, 6.1**

- [x] 2. Extend Spacing & ThemeExt
  - [x] 2.1 Add bottomNavSafeArea to Spacing object
    - Add `val bottomNavSafeArea = 100.dp` to the Spacing object
    - File: `shared/src/commonMain/kotlin/com/sdm3/parent/core/designsystem/theme/Spacing.kt`
    - _Requirements: 3.1, 3.2_

  - [x] 2.2 Add liquid glass helper functions to ThemeExt.kt
    - Add `liquidGlassSurfaceColor()`, `liquidGlassBorderColor()`, `liquidGlassHighlightColor()`, `liquidGlassShadowColor()` composable helper functions
    - File: `shared/src/commonMain/kotlin/com/sdm3/parent/core/designsystem/theme/ThemeExt.kt`
    - _Requirements: 1.1, 2.1_

- [x] 3. Update Sdm3GlassCard & Sdm3Card
  - [x] 3.1 Migrate Sdm3GlassCard to use new liquidGlass tokens
    - Replace `surfaceGlass` → `liquidGlassSurface`, `glassOutline` → `liquidGlassBorder`
    - Add `graphicsLayer` with hardware shadow using `liquidGlassShadow`
    - Set elevation to 0.dp (shadow via graphicsLayer)
    - File: `shared/src/commonMain/kotlin/com/sdm3/parent/core/designsystem/component/Sdm3GlassCard.kt`
    - _Requirements: 2.2, 8.1_

  - [x] 3.2 Update Sdm3Card to use liquidGlassBorder as default border
    - Add `graphicsLayer` shadow using `liquidGlassShadow`
    - Use `liquidGlassBorder` as default border when no border param is provided
    - Use `liquidGlassSurface` as container color
    - File: `shared/src/commonMain/kotlin/com/sdm3/parent/core/designsystem/component/Sdm3Card.kt`
    - _Requirements: 2.1, 8.1_

- [x] 4. Update SDM3BottomNavBar & Sdm3AdaptiveNav
  - [x] 4.1 Update SDM3BottomNavBar to use Liquid Glass tokens
    - Replace `glassSurfaceColor()`/`glassBorderColor()` with `ProductSchoolTheme.colors.liquidGlassSurface`/`liquidGlassBorder`
    - Update `graphicsLayer` shadow colors to use `liquidGlassShadow`
    - File: `shared/src/commonMain/kotlin/com/sdm3/parent/core/designsystem/component/SDM3BottomNavBar.kt`
    - _Requirements: 2.3, 8.1_

  - [x] 4.2 Update Sdm3AdaptiveNav to use Liquid Glass tokens
    - Replace any glass color references with new liquid glass tokens
    - File: `shared/src/commonMain/kotlin/com/sdm3/parent/core/designsystem/component/Sdm3AdaptiveNav.kt`
    - _Requirements: 2.3_

- [x] 5. Fix Shimmer & AtmosphericGlow
  - [x] 5.1 Fix Shimmer animation direction and gradient
    - Change `repeatMode` to `RepeatMode.Restart` for consistent single direction
    - Make gradient horizontal-only (start/end with same y coordinate)
    - Use proper token colors from MaterialTheme.colorScheme
    - File: `shared/src/commonMain/kotlin/com/sdm3/parent/core/designsystem/component/Shimmer.kt`
    - _Requirements: 2.6, 5.3_

  - [x] 5.2 Fix AtmosphericGlow with graphicsLayer and dark mode alpha cap
    - Add `graphicsLayer { clip = false }` for hardware layer
    - Cap dark mode alpha at maximum 0.15
    - Ensure component is reusable with configurable alignment and color
    - File: `shared/src/commonMain/kotlin/com/sdm3/parent/core/designsystem/component/AtmosphericGlow.kt`
    - _Requirements: 6.2, 8.2_

  - [ ]* 5.3 Write property test for dark mode atmospheric glow alpha cap
    - **Property 4: Dark mode atmospheric glow alpha cap**
    - **Validates: Requirements 6.2**

- [x] 6. Add pressEffect Modifier & Fix Badge
  - [x] 6.1 Add Modifier.pressEffect() to Modifiers.kt
    - Implement press state animation using scale feedback (0.96f pressed, 1f released)
    - Use `Sdm3Motion.easing` and `Sdm3Motion.durationFast` for animation spec
    - File: `shared/src/commonMain/kotlin/com/sdm3/parent/core/designsystem/component/Modifiers.kt`
    - _Requirements: 5.1_

  - [x] 6.2 Fix Badge to use container tokens and prevent clipping
    - Use `successContainer`, `warningContainer`, `dangerContainer`, `infoContainer` for badge backgrounds
    - Enforce minimum 10sp font size
    - Set `overflow = TextOverflow.Visible` to prevent clipping
    - Ensure minimum 48dp touch target with `defaultMinSize`
    - File: `shared/src/commonMain/kotlin/com/sdm3/parent/core/designsystem/component/Badge.kt`
    - _Requirements: 1.5, 2.5, 7.2, 7.3_

- [x] 7. Checkpoint - Ensure design system foundation compiles
  - Ensure all tests pass, ask the user if questions arise.

- [x] 8. Update HomeScreen
  - [x] 8.1 Apply Liquid Glass to HomeScreen
    - Replace hardcoded 120.dp bottom padding with `Spacing.bottomNavSafeArea`
    - Use `AtmosphericGlow` component for background decoration
    - Ensure all cards use `Sdm3Card`/`Sdm3GlassCard`
    - Replace any ad-hoc `Surface` composables with design system components
    - Add `contentDescription` to icons missing it
    - File: `shared/src/commonMain/kotlin/com/sdm3/parent/feature/home/ui/HomeScreen.kt`
    - _Requirements: 3.1, 3.2, 4.1, 7.1_

- [x] 9. Update NilaiRaporScreen
  - [x] 9.1 Apply Liquid Glass to NilaiRaporScreen and sub-screens
    - Replace hardcoded 100.dp spacers with `Spacing.bottomNavSafeArea`
    - Replace inline Canvas atmospheric glow with `AtmosphericGlow` component
    - Ensure grade cards use glass styling via `Sdm3GlassCard`
    - Add `contentDescription` to icons
    - Files: `shared/src/commonMain/kotlin/com/sdm3/parent/feature/nilai/ui/NilaiRaporScreen.kt`, `DetailNilaiMapelScreen.kt`
    - _Requirements: 3.1, 4.2, 7.1_

- [x] 10. Update PembayaranSppScreen & sub-screens
  - [x] 10.1 Apply Liquid Glass to PembayaranSppScreen and sub-screens
    - Replace `paymentBottomSafePadding()` or hardcoded values with `Spacing.bottomNavSafeArea`
    - Ensure payment cards use glass styling via `Sdm3GlassCard`
    - Add `contentDescription` to icons
    - Files: `shared/src/commonMain/kotlin/com/sdm3/parent/feature/pembayaran/ui/*.kt`
    - _Requirements: 3.1, 4.3, 7.1_

- [x] 11. Update HalamanRaporScreen & sub-screens
  - [x] 11.1 Apply Liquid Glass to HalamanRaporScreen and sub-screens
    - Replace hardcoded 100.dp spacers with `Spacing.bottomNavSafeArea`
    - Replace inline Canvas atmospheric glow with `AtmosphericGlow` component
    - Ensure rapor cards use glass styling
    - Add `contentDescription` to icons
    - Files: `shared/src/commonMain/kotlin/com/sdm3/parent/feature/rapor/ui/*.kt`
    - _Requirements: 3.1, 4.4, 7.1_

- [x] 12. Update ProfilAkunScreen
  - [x] 12.1 Apply Liquid Glass to ProfilAkunScreen
    - Replace hardcoded 100.dp spacers with `Spacing.bottomNavSafeArea`
    - Replace inline Canvas atmospheric glow with `AtmosphericGlow` component
    - Ensure profile sections and menu items use glass cards
    - Add `contentDescription` to icons
    - Files: `shared/src/commonMain/kotlin/com/sdm3/parent/feature/profil/ui/*.kt`
    - _Requirements: 3.1, 4.5, 7.1_

- [x] 13. Update remaining screens (auth, kehadiran, infoanak, notifikasi, guru)
  - [x] 13.1 Audit and update auth screens
    - Replace hardcoded spacing → use Spacing tokens
    - Replace inline Canvas → use AtmosphericGlow
    - Replace ad-hoc Surface → use design system components
    - Add missing contentDescription to icons
    - Files: `shared/src/commonMain/kotlin/com/sdm3/parent/feature/auth/ui/*.kt`
    - _Requirements: 4.6, 7.1, 8.3_

  - [x] 13.2 Audit and update kehadiran screens
    - Same audit pattern: spacing tokens, AtmosphericGlow, design system components, contentDescription
    - Files: `shared/src/commonMain/kotlin/com/sdm3/parent/feature/kehadiran/ui/*.kt`
    - _Requirements: 4.6, 7.1, 8.3_

  - [x] 13.3 Audit and update infoanak screens
    - Same audit pattern: spacing tokens, AtmosphericGlow, design system components, contentDescription
    - Files: `shared/src/commonMain/kotlin/com/sdm3/parent/feature/infoanak/ui/*.kt`
    - _Requirements: 4.6, 7.1, 8.3_

  - [x] 13.4 Audit and update notifikasi screens
    - Same audit pattern: spacing tokens, AtmosphericGlow, design system components, contentDescription
    - Files: `shared/src/commonMain/kotlin/com/sdm3/parent/feature/notifikasi/ui/*.kt`
    - _Requirements: 4.6, 7.1, 8.3_

  - [x] 13.5 Audit and update guru screens
    - Same audit pattern: spacing tokens, AtmosphericGlow, design system components, contentDescription
    - Files: `shared/src/commonMain/kotlin/com/sdm3/parent/feature/guru/ui/*.kt`
    - _Requirements: 4.6, 7.1, 8.3_

- [x] 14. Checkpoint - Ensure all screens compile and use design system
  - Ensure all tests pass, ask the user if questions arise.

- [x] 15. Remove deprecated backward-compat vals from Color.kt
  - [x] 15.1 Remove unused deprecated top-level color vals
    - Search for usages of deprecated top-level vals (Primary, OnPrimary, GlassSurface, GlassOutline, etc.)
    - Only remove vals with zero references across the entire codebase
    - Clean up any remaining hardcoded `Color()` usages found during the search
    - File: `shared/src/commonMain/kotlin/com/sdm3/parent/core/designsystem/theme/Color.kt`
    - _Requirements: 8.3, 8.4, 8.7_

- [x] 16. Final checkpoint - Ensure full compilation and no warnings
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation
- Property tests validate universal correctness properties from the design document
- The project is Kotlin Multiplatform — all code lives in `shared/src/commonMain/`
- Tasks 1–6 form the design system foundation; Tasks 8–13 are screen migrations that can partially parallelize
- Task 15 depends on all screen migrations being complete to ensure no references remain

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1"] },
    { "id": 1, "tasks": ["1.2", "2.1", "2.2", "3.1", "3.2", "4.1", "4.2", "5.1", "5.2", "6.1", "6.2"] },
    { "id": 2, "tasks": ["5.3", "8.1", "9.1", "10.1", "11.1", "12.1", "13.1", "13.2", "13.3", "13.4", "13.5"] },
    { "id": 3, "tasks": ["15.1"] }
  ]
}
```
