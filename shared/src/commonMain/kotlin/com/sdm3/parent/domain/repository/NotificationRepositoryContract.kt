package com.sdm3.parent.domain.repository

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.NotificationDto
import com.sdm3.parent.data.remote.dto.UnreadCountDto

interface NotificationRepositoryContract {
    suspend fun getNotifications(): ApiResult<List<NotificationDto>>
    suspend fun markAsRead(id: String): ApiResult<Unit>
    suspend fun markAllAsRead(): ApiResult<Unit>
    suspend fun getUnreadCount(): ApiResult<UnreadCountDto>
}
