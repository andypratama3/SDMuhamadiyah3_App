package com.sdm3.parent.data.remote.api

import com.sdm3.parent.getPlatformName
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.network.toApiResult
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class FcmApi(private val provider: HttpClientProvider) {

    suspend fun register(fcmToken: String, devicePlatform: String? = null): ApiResult<Unit> {
        val platform = devicePlatform ?: getPlatformName().lowercase()
        val response = provider.client.post {
            url(Endpoints.PARENT_FCM_REGISTER)
            provider.applyAuthHeader(this)
            setBody(FcmRegisterRequest(fcmToken = fcmToken, devicePlatform = platform))
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun unregister(): ApiResult<Unit> {
        val response = provider.client.post {
            url(Endpoints.PARENT_FCM_UNREGISTER)
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    @Serializable
    private data class FcmRegisterRequest(
        @SerialName("fcm_token")
        val fcmToken: String,
        @SerialName("device_platform")
        val devicePlatform: String? = "android"
    )
}
