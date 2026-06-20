package com.sdm3.parent.data.remote.api

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.network.toApiResult
import com.sdm3.parent.data.remote.dto.NotificationDto
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.url

class NotificationApi(private val provider: HttpClientProvider) {

    suspend fun getNotifications(): ApiResult<List<NotificationDto>> {
        val response = provider.client.get {
            url(Endpoints.PARENT_NOTIFICATIONS)
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun markAsRead(id: String): ApiResult<Unit> {
        val response = provider.client.post {
            url(Endpoints.PARENT_NOTIFICATION_READ.replace("{id}", id))
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }
}
