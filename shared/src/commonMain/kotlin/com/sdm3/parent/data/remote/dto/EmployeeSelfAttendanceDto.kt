package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmployeeSelfAttendanceTodayDto(
    val employee: EmployeeSelfProfileDto,
    val date: String,
    val attendance: EmployeeSelfAttendanceRecordDto? = null,
    val actions: EmployeeSelfAttendanceActionsDto,
)

@Serializable
data class EmployeeSelfProfileDto(
    val id: String,
    val name: String,
    val nip: String? = null,
)

@Serializable
data class EmployeeSelfAttendanceActionsDto(
    @SerialName("can_check_in")
    val canCheckIn: Boolean = false,
    @SerialName("can_check_out")
    val canCheckOut: Boolean = false,
)

@Serializable
data class EmployeeSelfAttendanceRecordDto(
    val id: String,
    val date: String? = null,
    @SerialName("check_in_time")
    val checkInTime: String? = null,
    @SerialName("check_out_time")
    val checkOutTime: String? = null,
    @SerialName("check_in_latitude")
    val checkInLatitude: Double? = null,
    @SerialName("check_in_longitude")
    val checkInLongitude: Double? = null,
    @SerialName("check_out_latitude")
    val checkOutLatitude: Double? = null,
    @SerialName("check_out_longitude")
    val checkOutLongitude: Double? = null,
    @SerialName("check_in_status")
    val checkInStatus: String? = null,
    @SerialName("check_in_distance")
    val checkInDistance: Double? = null,
    @SerialName("check_out_distance")
    val checkOutDistance: Double? = null,
    @SerialName("location_name")
    val locationName: String? = null,
)

@Serializable
data class EmployeeSelfCheckInRequest(
    val latitude: Double,
    val longitude: Double,
    @SerialName("device_id")
    val deviceId: String,
    @SerialName("device_fingerprint")
    val deviceFingerprint: String,
)

@Serializable
data class EmployeeSelfCheckOutRequest(
    val latitude: Double,
    val longitude: Double,
    @SerialName("device_id")
    val deviceId: String,
)
