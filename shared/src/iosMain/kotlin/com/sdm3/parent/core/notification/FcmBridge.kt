package com.sdm3.parent.core.notification

/**
 * Entry point for the iOS Swift layer to pass FCM tokens into Kotlin.
 */
object FcmBridge {
    fun onTokenReceived(token: String) {
        FcmTokenStore.updateToken(token)
        FcmRegistrationCoordinator.onTokenRefresh(token)
    }
}
