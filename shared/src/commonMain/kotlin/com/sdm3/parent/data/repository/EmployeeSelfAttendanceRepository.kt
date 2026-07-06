package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.safeApiCall
import com.sdm3.parent.data.remote.api.EmployeeSelfAttendanceApi
import com.sdm3.parent.data.remote.dto.EmployeeSelfAttendanceRecordDto
import com.sdm3.parent.data.remote.dto.EmployeeSelfAttendanceTodayDto
import com.sdm3.parent.data.remote.dto.EmployeeSelfCheckInRequest
import com.sdm3.parent.data.remote.dto.EmployeeSelfCheckOutRequest
import com.sdm3.parent.domain.repository.EmployeeSelfAttendanceRepositoryContract
import com.sdm3.parent.platform.DeviceIdentity

class EmployeeSelfAttendanceRepository(
    private val api: EmployeeSelfAttendanceApi,
) : EmployeeSelfAttendanceRepositoryContract {

    override suspend fun getToday(): ApiResult<EmployeeSelfAttendanceTodayDto> =
        safeApiCall("Gagal memuat absensi hari ini") { api.getToday() }

    override suspend fun checkIn(latitude: Double, longitude: Double): ApiResult<EmployeeSelfAttendanceRecordDto> =
        safeApiCall("Gagal check-in") {
            api.checkIn(
                EmployeeSelfCheckInRequest(
                    latitude = latitude,
                    longitude = longitude,
                    deviceId = DeviceIdentity.deviceId(),
                    deviceFingerprint = DeviceIdentity.deviceFingerprint(),
                ),
            )
        }

    override suspend fun checkOut(latitude: Double, longitude: Double): ApiResult<EmployeeSelfAttendanceRecordDto> =
        safeApiCall("Gagal check-out") {
            api.checkOut(
                EmployeeSelfCheckOutRequest(
                    latitude = latitude,
                    longitude = longitude,
                    deviceId = DeviceIdentity.deviceId(),
                ),
            )
        }
}
