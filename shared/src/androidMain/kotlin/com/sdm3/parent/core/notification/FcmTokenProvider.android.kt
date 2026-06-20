package com.sdm3.parent.core.notification

actual class FcmTokenProvider {
    private var cachedToken: String? = null

    actual suspend fun getToken(): String? {
        return cachedToken
    }

    actual fun onNewToken(token: String) {
        cachedToken = token
    }
}
