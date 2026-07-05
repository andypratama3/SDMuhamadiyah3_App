package com.sdm3.parent.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect

@Composable
actual fun ApplySystemBars() {
    val darkTheme = isSystemInDarkTheme()
    SideEffect {
        // Light theme → dark status bar icons; dark theme → light icons.
        StatusBarAppearanceController.apply(prefersDarkStatusBarIcons = !darkTheme)
    }
}
