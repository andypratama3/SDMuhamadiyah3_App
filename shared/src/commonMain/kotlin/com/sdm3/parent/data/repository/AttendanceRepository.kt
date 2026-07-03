package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
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
        return try {
            val result = api.getAttendances(studentId, month, year)
            if (result is ApiResult.Success) cache.cacheAttendances(safeStudentId, month ?: 0, year ?: 0, result.data)
            result
        } catch (e: Exception) {
            val cached = cache.getAttendances(safeStudentId, month ?: 0, year ?: 0)
            if (cached.isNotEmpty()) ApiResult.Success(cached)
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
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil ringkasan absensi"))
        }
    }
}
