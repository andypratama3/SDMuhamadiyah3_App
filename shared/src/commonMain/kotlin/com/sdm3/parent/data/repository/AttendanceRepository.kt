package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.AttendanceApi
import com.sdm3.parent.data.remote.dto.AttendanceDto
import com.sdm3.parent.data.remote.dto.AttendanceSummaryDto
import com.sdm3.parent.domain.repository.AttendanceRepositoryContract

class AttendanceRepository(private val api: AttendanceApi) : AttendanceRepositoryContract {

    override suspend fun getAttendances(studentId: String, month: Int?, year: Int?): ApiResult<List<AttendanceDto>> {
        return try {
            api.getAttendances(studentId, month, year)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data absensi"))
        }
    }

    override suspend fun getAttendanceSummary(studentId: String): ApiResult<AttendanceSummaryDto> {
        return try {
            api.getAttendanceSummary(studentId)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil ringkasan absensi"))
        }
    }
}
