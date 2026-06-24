package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.NotificationApi
import com.sdm3.parent.data.remote.dto.NotificationDto
import com.sdm3.parent.domain.repository.NotificationRepositoryContract

class NotificationRepository(private val api: NotificationApi) : NotificationRepositoryContract {

    override suspend fun getNotifications(): ApiResult<List<NotificationDto>> {
        return try {
            api.getNotifications()
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil notifikasi"))
        }
    }

    override suspend fun markAsRead(id: String): ApiResult<Unit> {
        return try {
            api.markAsRead(id)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal menandai notifikasi"))
        }
    }
}
