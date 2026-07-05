package com.sdm3.parent.core.notification

actual class FcmTokenProvider {
    actual suspend fun getToken(): String? = FcmTokenStore.getCachedToken()

    actual fun onNewToken(token: String) {
        FcmTokenStore.updateToken(token)
        FcmRegistrationCoordinator.onTokenRefresh(token)
    }

    actual fun requestPermissionIfNeeded() {
        // iOS: izin push diminta sekali di AppDelegate (Swift) agar tidak dobel prompt.
    }
}
