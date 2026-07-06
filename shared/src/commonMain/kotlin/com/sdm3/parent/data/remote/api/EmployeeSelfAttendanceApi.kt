package com.sdm3.parent.data.remote.api

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.network.toApiResult
import com.sdm3.parent.data.remote.dto.EmployeeSelfAttendanceRecordDto
import com.sdm3.parent.data.remote.dto.EmployeeSelfAttendanceTodayDto
import com.sdm3.parent.data.remote.dto.EmployeeSelfCheckInRequest
import com.sdm3.parent.data.remote.dto.EmployeeSelfCheckOutRequest
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class EmployeeSelfAttendanceApi(private val provider: HttpClientProvider) {

    suspend fun getToday(): ApiResult<EmployeeSelfAttendanceTodayDto> {
        val response = provider.client.get {
            url(Endpoints.TEACHER_MY_ATTENDANCE_TODAY)
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun checkIn(request: EmployeeSelfCheckInRequest): ApiResult<EmployeeSelfAttendanceRecordDto> {
        val response = provider.client.post {
            url(Endpoints.TEACHER_MY_ATTENDANCE_CHECK_IN)
            provider.applyAuthHeader(this)
            setBody(request)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun checkOut(request: EmployeeSelfCheckOutRequest): ApiResult<EmployeeSelfAttendanceRecordDto> {
        val response = provider.client.post {
            url(Endpoints.TEACHER_MY_ATTENDANCE_CHECK_OUT)
            provider.applyAuthHeader(this)
            setBody(request)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }
}
