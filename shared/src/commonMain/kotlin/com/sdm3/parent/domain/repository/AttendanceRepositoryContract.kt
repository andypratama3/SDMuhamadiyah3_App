package com.sdm3.parent.domain.repository

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.AttendanceDto
import com.sdm3.parent.data.remote.dto.AttendanceSummaryDto

interface AttendanceRepositoryContract {
    suspend fun getAttendances(studentId: String, month: Int? = null, year: Int? = null): ApiResult<List<AttendanceDto>>
    suspend fun getAttendanceSummary(studentId: String): ApiResult<AttendanceSummaryDto>
}
