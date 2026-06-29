package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.di.DevMode
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.dummy.DummyDataProvider
import com.sdm3.parent.data.remote.api.NotificationApi
import com.sdm3.parent.data.remote.dto.NotificationDto
import com.sdm3.parent.domain.repository.NotificationRepositoryContract

class NotificationRepository(
    private val api: NotificationApi,
    private val cache: CacheDataSource,
) : NotificationRepositoryContract {

    override suspend fun getNotifications(): ApiResult<List<NotificationDto>> {
        return try {
            val result = api.getNotifications()
            if (result is ApiResult.Success) cache.cacheNotifications(result.data)
            result
        } catch (e: Exception) {
            val cached = cache.getNotifications()
            if (cached.isNotEmpty()) ApiResult.Success(cached)
            else if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummyNotifications)
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil notifikasi"))
        }
    }

    override suspend fun markAsRead(id: String): ApiResult<Unit> {
        return try {
            api.markAsRead(id)
        } catch (e: Exception) {
            if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummyNotificationMarkedAsRead(id))
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal menandai notifikasi"))
        }
    }
}
