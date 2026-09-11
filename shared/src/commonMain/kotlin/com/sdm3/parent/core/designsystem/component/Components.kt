package com.sdm3.parent.core.designsystem.component

/**
 * SDM3 Design System Components Index
 * 
 * This file provides a centralized export of all design system components.
 * Import this file to access all components at once.
 * 
 * ## Core Components
 * - [Sdm3Button] - Primary button with haptic feedback and loading state
 * - [Sdm3OutlinedButton] - Outlined button variant
 * - [Sdm3TextField] - Glassmorphic text input with error handling
 * - [Sdm3Card] - Basic card container
 * - [Sdm3GlassCard] - Glassmorphic card with blur effect
 * 
 * ## Status & Feedback Components
 * - [StatusChip] - Status indicator chip with dot
 * - [NotificationBadge] - Badge for notification counts
 * - [Sdm3EmptyState] - Empty state placeholder
 * - [Sdm3ErrorState] - Error state placeholder
 * - [Sdm3ErrorBanner] - Inline error banner for forms
 * - [Sdm3CategoryBadge] - Uppercase category badge pill
 * - [Sdm3IconBadge] - Icon-in-surface badge container
 * - [Sdm3InfoRow] - Label-value info row
 * 
 * ## Screen Components
 * - [ScreenUiState] - Shared sealed UI state (Loading/Empty/Error/Success)
 * - [resolveScreenState] - Helper to resolve ViewModel state booleans
 * - [ScreenScaffold] - Standard scaffold with TopAppBar + AtmosphericGlow
 * - [ScreenGlowBackground] - AtmosphericGlow background wrapper
 * - [Sdm3GlassIconContainer] - Glass container for logos/icons
 * 
 * ## Progress Components
 * - [Shimmer] - Shimmer loading effect
 * 
 * ## Layout Components
 * - [SDM3BottomNavBar] - Bottom navigation bar
 * - [Sdm3AdaptiveLayout] - Adaptive layout for different screen sizes
 * - [SectionHeader] - Section header with optional action
 * 
 * ## Visual Components
 * - [Sdm3Logo] - App logo
 * - [AtmosphericGlow] - Atmospheric background glow
 * 
 * ## Modifier Extensions
 * - [pressEffect] - Press scale effect
 * - [shimmerEffect] - Shimmer loading effect
 * 
 * ## Theme Extensions
 * - [statusSuccessColor] - Success green color
 * - [statusWarningColor] - Warning orange color
 * - [statusDangerColor] - Danger red color
 * - [statusInfoColor] - Info blue color
 * - [heroContentColor] - Hero content color
 * - [glassSurfaceColor] - Glass surface color
 * - [glassBorderColor] - Glass border color
 * - [overlayScrimColor] - Overlay scrim color
 */

// Re-export all components for easy access
// Note: Individual imports are still preferred for explicit dependencies