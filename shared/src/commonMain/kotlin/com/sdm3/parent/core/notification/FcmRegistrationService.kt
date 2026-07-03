package com.sdm3.parent.core.notification

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.security.SecureTokenManager
import com.sdm3.parent.data.remote.api.FcmApi

class FcmRegistrationService(
    private val fcmApi: FcmApi,
    private val fcmTokenProvider: FcmTokenProvider,
    private val secureTokenManager: SecureTokenManager,
) : FcmRegistrar {
    override suspend fun registerIfAvailable() {
        val token = fcmTokenProvider.getToken() ?: secureTokenManager.getFcmToken() ?: return
        when (fcmApi.register(token)) {
            is ApiResult.Success -> secureTokenManager.saveFcmToken(token)
            is ApiResult.Error -> Unit
        }
    }

    override suspend fun unregisterIfNeeded() {
        if (secureTokenManager.getFcmToken() == null) return
        fcmApi.unregister()
        secureTokenManager.clearFcmToken()
        FcmTokenStore.clear()
    }
}
