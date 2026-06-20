package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.NotificationApi
import com.sdm3.parent.data.remote.dto.NotificationDto

class NotificationRepository(private val api: NotificationApi) {

    suspend fun getNotifications(): ApiResult<List<NotificationDto>> {
        return try {
            api.getNotifications()
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil notifikasi"))
        }
    }

    suspend fun markAsRead(id: String): ApiResult<Unit> {
        return try {
            api.markAsRead(id)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal menandai notifikasi"))
        }
    }
}
