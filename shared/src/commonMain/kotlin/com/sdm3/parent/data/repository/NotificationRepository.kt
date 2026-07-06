package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.safeApiCall
import com.sdm3.parent.core.network.safeApiCallWithCache
import com.sdm3.parent.data.remote.api.NotificationApi
import com.sdm3.parent.data.remote.dto.NotificationDto
import com.sdm3.parent.data.remote.dto.UnreadCountDto
import com.sdm3.parent.domain.repository.NotificationRepositoryContract

class NotificationRepository(
    private val api: NotificationApi,
    private val cache: CacheDataSource,
) : NotificationRepositoryContract {

    override suspend fun getNotifications(): ApiResult<List<NotificationDto>> =
        safeApiCallWithCache(
            fallback = "Gagal mengambil notifikasi",
            block = {
                when (val result = api.getNotifications()) {
                    is ApiResult.Success -> {
                        cache.cacheNotifications(result.data)
                        result
                    }
                    is ApiResult.Error -> result
                }
            },
            cacheFallback = { cache.getNotifications().takeIf { it.isNotEmpty() } },
        )

    override suspend fun markAsRead(id: String): ApiResult<Unit> =
        safeApiCall("Gagal menandai notifikasi") { api.markAsRead(id) }

    override suspend fun markAllAsRead(): ApiResult<Unit> =
        safeApiCall("Gagal menandai semua notifikasi") { api.markAllAsRead() }

    override suspend fun getUnreadCount(): ApiResult<UnreadCountDto> =
        safeApiCall("Gagal mengambil jumlah notifikasi") { api.getUnreadCount() }
}
