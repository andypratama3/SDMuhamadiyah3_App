package com.sdm3.parent

import androidx.compose.ui.window.ComposeUIViewController
import com.sdm3.parent.core.di.allAppModules
import org.koin.compose.KoinApplication

fun MainViewController() = ComposeUIViewController {
    KoinApplication(application = {
        modules(allAppModules)
    }) {
        App()
    }
}
