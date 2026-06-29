package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.di.DevMode
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.dummy.DummyDataProvider
import com.sdm3.parent.data.remote.api.AttendanceApi
import com.sdm3.parent.data.remote.dto.AttendanceDto
import com.sdm3.parent.data.remote.dto.AttendanceSummaryDto
import com.sdm3.parent.domain.repository.AttendanceRepositoryContract

class AttendanceRepository(
    private val api: AttendanceApi,
    private val cache: CacheDataSource,
) : AttendanceRepositoryContract {

    override suspend fun getAttendances(studentId: String, month: Int?, year: Int?): ApiResult<List<AttendanceDto>> {
        val m = month ?: 2
        val y = year ?: 2026
        return try {
            val result = api.getAttendances(studentId, month, year)
            if (result is ApiResult.Success) cache.cacheAttendances(studentId, m, y, result.data)
            result
        } catch (e: Exception) {
            val cached = cache.getAttendances(studentId, m, y)
            if (cached.isNotEmpty()) ApiResult.Success(cached)
            else if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummyAttendancesInMonth(m, y))
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data absensi"))
        }
    }

    override suspend fun getAttendanceSummary(studentId: String): ApiResult<AttendanceSummaryDto> {
        return try {
            val result = api.getAttendanceSummary(studentId)
            if (result is ApiResult.Success) cache.cacheAttendanceSummary(studentId, result.data)
            result
        } catch (e: Exception) {
            val cached = cache.getAttendanceSummary(studentId)
            if (cached != null) ApiResult.Success(cached)
            else if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummyAttendanceSummary)
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil ringkasan absensi"))
        }
    }
}
