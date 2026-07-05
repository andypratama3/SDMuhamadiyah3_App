package com.sdm3.parent

import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import com.sdm3.parent.core.designsystem.StatusBarHostViewController
import com.sdm3.parent.core.di.allAppModules
import com.sdm3.parent.platform.installIosPlatformBindings
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration
import org.koin.plugin.module.dsl.koinConfiguration
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    installIosPlatformBindings()
    val composeController = ComposeUIViewController(
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
    return StatusBarHostViewController(composeController)
}
