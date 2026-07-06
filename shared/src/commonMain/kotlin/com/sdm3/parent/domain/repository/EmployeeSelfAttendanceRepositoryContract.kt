package com.sdm3.parent.domain.repository

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.EmployeeSelfAttendanceRecordDto
import com.sdm3.parent.data.remote.dto.EmployeeSelfAttendanceTodayDto

interface EmployeeSelfAttendanceRepositoryContract {
    suspend fun getToday(): ApiResult<EmployeeSelfAttendanceTodayDto>
    suspend fun checkIn(latitude: Double, longitude: Double): ApiResult<EmployeeSelfAttendanceRecordDto>
    suspend fun checkOut(latitude: Double, longitude: Double): ApiResult<EmployeeSelfAttendanceRecordDto>
}
