package com.sdm3.parent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.sdm3.parent.core.designsystem.ApplySystemBars
import com.sdm3.parent.core.designsystem.theme.SDM3Theme
import com.sdm3.parent.core.navigation.SDM3NavHost
import com.sdm3.parent.core.navigation.SDM3Route
import com.sdm3.parent.core.notification.FcmRegistrar
import com.sdm3.parent.core.notification.FcmRegistrationCoordinator
import com.sdm3.parent.core.notification.FcmTokenProvider
import com.sdm3.parent.core.security.InstallState
import com.sdm3.parent.core.security.SecureTokenManager
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun App() {
    val isPreview = LocalInspectionMode.current
    if (!isPreview) {
        val fcmRegistrar: FcmRegistrar = koinInject()
        val secureTokenManager: SecureTokenManager = koinInject()
        val fcmTokenProvider: FcmTokenProvider = koinInject()
        val installState: InstallState = koinInject()

        // Bersihkan sisa data lama pada (re)install baru (khusus iOS: Keychain
        // bertahan setelah uninstall). Harus dijalankan sebelum Splash memutuskan
        // tujuan navigasi, sehingga onboarding & login muncul dari awal.
        LaunchedEffect(installState, secureTokenManager) {
            if (installState.isFreshInstall()) {
                secureTokenManager.resetForFreshInstall()
                installState.markInstalled()
            }
        }

        LaunchedEffect(fcmRegistrar, secureTokenManager) {
            FcmRegistrationCoordinator.bind(fcmRegistrar, secureTokenManager)
        }

        LaunchedEffect(fcmTokenProvider) {
            if (getPlatformName() == "Android") {
                fcmTokenProvider.requestPermissionIfNeeded()
            }
            // Debug Android: cetak FCM token ke Logcat. iOS dicetak dari AppDelegate Swift.
            if (isDebugBuild() && getPlatformName() == "Android") {
                delay(1500)
                val token = fcmTokenProvider.getToken()
                println(
                    if (token.isNullOrBlank()) {
                        "SDM3_FCM_TOKEN => null (menunggu callback Firebase)"
                    } else {
                        "SDM3_FCM_TOKEN => $token"
                    }
                )
            }
        }
    }

    SDM3Theme {
        ApplySystemBars()
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
