package com.sdm3.parent.domain.repository

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.NotificationDto

interface NotificationRepositoryContract {
    suspend fun getNotifications(): ApiResult<List<NotificationDto>>
    suspend fun markAsRead(id: String): ApiResult<Unit>
}
