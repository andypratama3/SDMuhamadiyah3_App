package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.di.DevMode
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.dummy.DummyDataProvider
import com.sdm3.parent.data.remote.api.DashboardApi
import com.sdm3.parent.data.remote.dto.DashboardDto
import com.sdm3.parent.domain.repository.DashboardRepositoryContract

class DashboardRepository(
    private val api: DashboardApi,
    private val cache: CacheDataSource,
) : DashboardRepositoryContract {

    override suspend fun getDashboard(studentId: String): ApiResult<DashboardDto> {
        return try {
            val result = api.getDashboard(studentId)
            if (result is ApiResult.Success) {
                val d = result.data
                cache.cacheStudent(d.student)
                d.attendanceSummary?.let { cache.cacheAttendanceSummary(studentId, it) }
                d.recentGrades?.let { cache.cacheGrades(studentId, it) }
                d.activeFees?.let { cache.cacheFees(studentId, it) }
                d.announcements?.let { cache.cacheArticles(it) }
            }
            result
        } catch (e: Exception) {
            if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummyDashboard(studentId))
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data dashboard"))
        }
    }
}
