package com.sdm3.parent.core.notification

/**
 * In-memory FCM token cache shared between platform layers and [FcmTokenProvider].
 */
object FcmTokenStore {
    private var token: String? = null

    fun updateToken(newToken: String) {
        token = newToken
    }

    fun getCachedToken(): String? = token

    fun clear() {
        token = null
    }
}
