package com.sdm3.parent.data.remote.api

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.network.toApiResult
import com.sdm3.parent.data.remote.dto.AttendanceDto
import com.sdm3.parent.data.remote.dto.AttendanceSummaryDto
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

class AttendanceApi(private val provider: HttpClientProvider) {

    suspend fun getAttendances(studentId: String, month: Int? = null, year: Int? = null): ApiResult<List<AttendanceDto>> {
        val response = provider.client.get {
            url(Endpoints.PARENT_ATTENDANCES)
            provider.applyAuthHeader(this)
            parameter("student_id", studentId)
            month?.let { parameter("month", it) }
            year?.let { parameter("year", it) }
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun getAttendanceSummary(studentId: String): ApiResult<AttendanceSummaryDto> {
        val response = provider.client.get {
            url(Endpoints.PARENT_ATTENDANCE_SUMMARY)
            provider.applyAuthHeader(this)
            parameter("student_id", studentId)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }
}
