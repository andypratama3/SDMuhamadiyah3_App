package com.sdm3.parent

import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import com.sdm3.parent.core.di.allAppModules
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration
import org.koin.plugin.module.dsl.koinConfiguration

fun MainViewController() = ComposeUIViewController(
    configure = {
        // Layar auth sudah memakai imePadding + verticalScroll sendiri.
        // Default FocusableAboveKeyboard bentrok dan memicu gesture/keyboard macet di iOS.
        onFocusBehavior = OnFocusBehavior.DoNothing
    }
) {
    KoinApplication(configuration = koinConfiguration {
        modules(allAppModules)
    }) {
        App()
    }
}
