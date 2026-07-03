package com.sdm3.parent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.sdm3.parent.core.designsystem.theme.SDM3Theme
import com.sdm3.parent.core.navigation.SDM3NavHost
import com.sdm3.parent.core.navigation.SDM3Route
import com.sdm3.parent.core.notification.FcmRegistrar
import com.sdm3.parent.core.notification.FcmRegistrationCoordinator
import com.sdm3.parent.core.notification.FcmTokenProvider
import com.sdm3.parent.core.security.SecureTokenManager
import org.koin.compose.koinInject

@Composable
fun App() {
    val isPreview = LocalInspectionMode.current
    if (!isPreview) {
        val fcmRegistrar: FcmRegistrar = koinInject()
        val secureTokenManager: SecureTokenManager = koinInject()
        val fcmTokenProvider: FcmTokenProvider = koinInject()

        LaunchedEffect(fcmRegistrar, secureTokenManager) {
            FcmRegistrationCoordinator.bind(fcmRegistrar, secureTokenManager)
        }

        LaunchedEffect(fcmTokenProvider) {
            fcmTokenProvider.requestPermissionIfNeeded()
        }
    }

    SDM3Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SDM3NavHost(
                startDestination = SDM3Route.Splash,
            )
        }
    }
}
