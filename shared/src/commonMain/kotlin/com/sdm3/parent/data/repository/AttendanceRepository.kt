package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.AttendanceApi
import com.sdm3.parent.data.remote.dto.AttendanceDto
import com.sdm3.parent.data.remote.dto.AttendanceSummaryDto

class AttendanceRepository(private val api: AttendanceApi) {

    suspend fun getAttendances(studentId: String, month: Int? = null, year: Int? = null): ApiResult<List<AttendanceDto>> {
        return try {
            api.getAttendances(studentId, month, year)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data absensi"))
        }
    }

    suspend fun getAttendanceSummary(studentId: String): ApiResult<AttendanceSummaryDto> {
        return try {
            api.getAttendanceSummary(studentId)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil ringkasan absensi"))
        }
    }
}
