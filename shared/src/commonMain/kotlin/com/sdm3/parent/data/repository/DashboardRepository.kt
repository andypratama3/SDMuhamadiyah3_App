package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.safeApiCall
import com.sdm3.parent.data.remote.api.DashboardApi
import com.sdm3.parent.data.remote.dto.DashboardDto
import com.sdm3.parent.domain.repository.DashboardRepositoryContract

class DashboardRepository(
    private val api: DashboardApi,
    private val cache: CacheDataSource,
) : DashboardRepositoryContract {

    override suspend fun getDashboard(studentId: String): ApiResult<DashboardDto> =
        safeApiCall("Gagal mengambil data dashboard") {
            when (val result = api.getDashboard(studentId)) {
                is ApiResult.Success -> {
                    val d = result.data
                    d.student?.let { cache.cacheStudent(it) }
                    d.attendanceSummary?.let { cache.cacheAttendanceSummary(studentId, it) }
                    d.recentGrades?.let { cache.cacheGrades(studentId, it) }
                    d.activeFees?.let { cache.cacheFees(studentId, it) }
                    d.announcements?.let { cache.cacheArticles(it) }
                    result
                }
                is ApiResult.Error -> result
            }
        }
}
