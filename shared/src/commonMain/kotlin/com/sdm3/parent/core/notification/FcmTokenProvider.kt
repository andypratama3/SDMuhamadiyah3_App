package com.sdm3.parent.core.notification

expect class FcmTokenProvider() {
    suspend fun getToken(): String?
    fun onNewToken(token: String)
}
