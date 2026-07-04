package com.sdm3.parent.core.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

actual class FcmTokenProvider {
    actual suspend fun getToken(): String? {
        FcmTokenStore.getCachedToken()?.let { return it }
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            Log.d(TAG, "FCM token retrieved: $token")
            FcmTokenStore.updateToken(token)
            token
        } catch (e: Exception) {
            Log.e(TAG, "Fetching FCM registration token failed", e)
            null
        }
    }

    actual fun onNewToken(token: String) {
        Log.d(TAG, "FCM token refreshed: $token")
        FcmTokenStore.updateToken(token)
        FcmRegistrationCoordinator.onTokenRefresh(token)
    }

    actual fun requestPermissionIfNeeded() {
        // Android 13+ permission is requested from MainActivity.
    }

    companion object {
        private const val TAG = "FcmTokenProvider"
    }
}
