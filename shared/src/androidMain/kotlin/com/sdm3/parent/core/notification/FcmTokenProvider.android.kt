package com.sdm3.parent.core.notification

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

actual class FcmTokenProvider {
    actual suspend fun getToken(): String? {
        FcmTokenStore.getCachedToken()?.let { return it }
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            FcmTokenStore.updateToken(token)
            token
        } catch (_: Exception) {
            null
        }
    }

    actual fun onNewToken(token: String) {
        FcmTokenStore.updateToken(token)
        FcmRegistrationCoordinator.onTokenRefresh(token)
    }

    actual fun requestPermissionIfNeeded() {
        // Android 13+ permission is requested from MainActivity.
    }
}
