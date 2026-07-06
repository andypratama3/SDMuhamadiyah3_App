package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.safeApiCallWithCache
import com.sdm3.parent.data.remote.api.AttendanceApi
import com.sdm3.parent.data.remote.dto.AttendanceDto
import com.sdm3.parent.data.remote.dto.AttendanceSummaryDto
import com.sdm3.parent.domain.repository.AttendanceRepositoryContract

class AttendanceRepository(
    private val api: AttendanceApi,
    private val cache: CacheDataSource,
) : AttendanceRepositoryContract {

    override suspend fun getAttendances(studentId: String?, month: Int?, year: Int?): ApiResult<List<AttendanceDto>> {
        val safeStudentId = studentId ?: ""
        val safeMonth = month ?: 0
        val safeYear = year ?: 0
        return safeApiCallWithCache(
            fallback = "Gagal mengambil data absensi",
            block = {
                when (val result = api.getAttendances(studentId, month, year)) {
                    is ApiResult.Success -> {
                        cache.cacheAttendances(safeStudentId, safeMonth, safeYear, result.data)
                        result
                    }
                    is ApiResult.Error -> result
                }
            },
            cacheFallback = {
                cache.getAttendances(safeStudentId, safeMonth, safeYear).takeIf { it.isNotEmpty() }
            },
        )
    }

    override suspend fun getAttendanceSummary(studentId: String): ApiResult<AttendanceSummaryDto> =
        safeApiCallWithCache(
            fallback = "Gagal mengambil ringkasan absensi",
            block = {
                when (val result = api.getAttendanceSummary(studentId)) {
                    is ApiResult.Success -> {
                        cache.cacheAttendanceSummary(studentId, result.data)
                        result
                    }
                    is ApiResult.Error -> result
                }
            },
            cacheFallback = { cache.getAttendanceSummary(studentId) },
        )
}
