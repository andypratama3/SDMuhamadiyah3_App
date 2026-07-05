package com.sdm3.parent.data.remote.api

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.network.toApiResult
import com.sdm3.parent.data.remote.dto.NotificationSettingsDto
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class NotificationPreferencesApi(private val provider: HttpClientProvider) {

    suspend fun getPreferences(): ApiResult<NotificationSettingsDto> {
        val response = provider.client.get {
            url(Endpoints.PARENT_NOTIFICATION_PREFERENCES)
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun updatePreferences(body: NotificationSettingsDto): ApiResult<NotificationSettingsDto> {
        val response = provider.client.patch {
            url(Endpoints.PARENT_NOTIFICATION_PREFERENCES)
            provider.applyAuthHeader(this)
            setBody(body)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }
}
