package com.sdm3.parent

import androidx.compose.ui.window.ComposeUIViewController
import com.sdm3.parent.core.di.allAppModules
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration
import org.koin.plugin.module.dsl.koinConfiguration

fun MainViewController() = ComposeUIViewController {
    KoinApplication(configuration = koinConfiguration {
        modules(allAppModules)
    }) {
        App()
    }
}
