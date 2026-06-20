package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceDto(
    val id: String,
    val date: String,
    val status: String,
    val notes: String? = null
)

@Serializable
data class AttendanceSummaryDto(
    val hadir: Int = 0,
    val sakit: Int = 0,
    val izin: Int = 0,
    val alpa: Int = 0
)
