# Requirements Document

## Introduction

Overhaul seluruh UI aplikasi SD Muhammadiyah 3 Samarinda untuk menggunakan **Liquid Glass** design system secara konsisten di semua screen dan komponen. Memastikan tidak ada tabrakan warna, bug visual, atau inkonsistensi. Seluruh kode harus production-ready.

## Glossary

- **Liquid_Glass**: Design system berbasis glassmorphism dengan frosted glass surfaces, subtle transparency, dan blur effects yang konsisten.
- **SDM3_Parent_App**: Aplikasi mobile Kotlin Multiplatform untuk orang tua murid SD Muhammadiyah 3 Samarinda.
- **Glass_Surface**: Komponen surface semi-transparan dengan blur effect yang menjadi dasar visual Liquid Glass.
- **Design_System_Component**: Komponen UI reusable yang terdefinisi di package `core/designsystem/component/`.
- **Atmospheric_Glow**: Radial gradient subtle sebagai background dekorasi untuk menambah depth visual.
- **Touch_Target**: Area minimum yang dapat disentuh user pada interactive element.
- **Contrast_Ratio**: Rasio luminance antara foreground text dan background color, minimum 4.5:1 untuk WCAG AA.
- **Spacing_Token**: Konstanta spacing terdefinisi di `Spacing` object untuk konsistensi layout.
- **Status_Color**: Warna semantic untuk state: success (hijau), warning (oranye), danger (merah), info (biru).

## Requirements

### Requirement 1: Liquid Glass Color System

**User Story:** As a developer, I want a well-defined Liquid Glass color token system, so that all glass surfaces use consistent transparency and there are no color clashes.

#### Acceptance Criteria

1. THE Design_System_Component SHALL define extended color tokens `liquidGlassSurface`, `liquidGlassBorder`, `liquidGlassHighlight`, and `liquidGlassShadow` for both light and dark mode.
2. WHILE the SDM3_Parent_App is in light mode, THE Glass_Surface SHALL use alpha values between 0.55 and 0.85 for surface transparency.
3. WHILE the SDM3_Parent_App is in dark mode, THE Glass_Surface SHALL use alpha values between 0.70 and 0.90 for surface transparency.
4. THE Design_System_Component SHALL ensure a minimum contrast ratio of 4.5:1 between text color and background color on all Glass_Surface elements.
5. THE Design_System_Component SHALL provide container variant colors for each Status_Color (success, warning, danger, info) suitable for badge backgrounds.

### Requirement 2: Component Consistency

**User Story:** As a developer, I want all UI components to use Liquid Glass tokens consistently, so that the visual appearance is unified across the entire app.

#### Acceptance Criteria

1. THE `Sdm3Card` component SHALL use Liquid_Glass color tokens for its surface, border, and shadow.
2. THE `Sdm3GlassCard` component SHALL use the same Liquid_Glass color tokens as `Sdm3Card` for visual consistency.
3. THE `SDM3BottomNavBar` component SHALL use Liquid_Glass surface color with proper blur effect and border styling.
4. WHEN a screen file contains an ad-hoc `Surface` composable, THE developer SHALL replace the ad-hoc Surface with a Design_System_Component equivalent.
5. THE badge component displaying "SEGERA" text SHALL render fully visible without clipping at any text size.
6. THE shimmer loading animation SHALL use consistent gradient colors and animation duration across all screen files.

### Requirement 3: Layout and Spacing

**User Story:** As a developer, I want consistent spacing and layout rules, so that all screens have proper safe areas and touch targets.

#### Acceptance Criteria

1. WHEN a screen is displayed as a tab (Home, Nilai, Bayar, Rapor, Profil), THE screen content SHALL include bottom safe area padding using `Spacing.bottomNavSafeArea`.
2. THE SDM3_Parent_App SHALL use `Spacing.bottomNavSafeArea` token instead of hardcoded numeric values for bottom navigation padding.
3. THE grid items in "Layanan Sekolah" section SHALL have a minimum Touch_Target size of 48dp in both width and height.
4. THE screen content SHALL use `Spacing.lg` (20dp) for horizontal padding consistently across all screens.

### Requirement 4: Screen-Level Liquid Glass Integration

**User Story:** As a user, I want every screen in the app to have a cohesive Liquid Glass visual style, so that my experience feels polished and premium.

#### Acceptance Criteria

1. THE HomeScreen SHALL use Liquid_Glass styling for all card surfaces, hero sections, and list items.
2. THE NilaiRaporScreen SHALL use Atmospheric_Glow background and Liquid_Glass styling for grade cards.
3. THE PembayaranSppScreen SHALL use Liquid_Glass styling for all payment cards and summary sections.
4. THE HalamanRaporScreen SHALL use Liquid_Glass styling for rapor cards and detail sections.
5. THE ProfilAkunScreen SHALL use Liquid_Glass styling for all profile sections and menu items.
6. WHEN a sub-screen is displayed, THE sub-screen SHALL use Design_System_Component composables instead of ad-hoc styled elements.

### Requirement 5: Animation and Motion

**User Story:** As a user, I want smooth animations and press feedback, so that interactions feel responsive and premium.

#### Acceptance Criteria

1. WHEN a user presses an interactive element, THE element SHALL display a press state animation using scale or alpha feedback.
2. WHEN navigating between screens, THE SDM3_Parent_App SHALL use `Sdm3Motion.easing` for transition animations.
3. THE shimmer animation SHALL move in a single consistent direction with smooth easing.

### Requirement 6: Dark Mode Compatibility

**User Story:** As a user, I want the app to look correct in dark mode, so that I can use it comfortably in low-light environments.

#### Acceptance Criteria

1. WHILE the SDM3_Parent_App is in dark mode, THE Glass_Surface SHALL use dark mode Liquid_Glass tokens with appropriate elevated transparency.
2. WHILE the SDM3_Parent_App is in dark mode, THE Atmospheric_Glow SHALL use reduced alpha values (maximum 0.15) to remain subtle.
3. WHILE the SDM3_Parent_App is in dark mode, THE text colors SHALL maintain a minimum Contrast_Ratio of 4.5:1 against their background.

### Requirement 7: Accessibility

**User Story:** As a user with accessibility needs, I want the app to be usable with assistive technologies, so that I can navigate and understand all content.

#### Acceptance Criteria

1. THE Design_System_Component icons SHALL include a non-empty `contentDescription` parameter for screen reader support.
2. THE interactive elements SHALL have a minimum Touch_Target size of 48dp.
3. THE badge text SHALL use a minimum font size of 10sp for readability.

### Requirement 8: Performance and Code Quality

**User Story:** As a developer, I want production-ready code with optimal performance, so that the app runs smoothly and is maintainable.

#### Acceptance Criteria

1. THE Glass_Surface composables SHALL use `graphicsLayer` modifier for hardware-accelerated rendering of blur and transparency effects.
2. THE Atmospheric_Glow backgrounds SHALL be implemented as a reusable component using `Canvas` with `graphicsLayer`.
3. THE codebase SHALL contain no hardcoded numeric values for spacing, color alpha, or sizing — all values SHALL use defined tokens.
4. THE codebase SHALL contain no unused parameters or ignored callback lambdas in composable functions.
5. WHEN a screen composable is defined, THE screen SHALL have an accompanying `@Preview` annotated composable function.
6. THE shared module SHALL compile without errors on both Android and iOS targets in commonMain source set.
7. THE shared module SHALL produce no deprecation warnings during compilation.
