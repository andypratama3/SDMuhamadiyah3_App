package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DashboardDto(
    val student: StudentDto,
    @SerialName("attendance_summary")
    val attendanceSummary: AttendanceSummaryDto? = null,
    @SerialName("recent_grades")
    val recentGrades: List<GradeDto>? = null,
    @SerialName("active_fees")
    val activeFees: List<StudentFeeDto>? = null,
    val announcements: List<ArticleDto>? = null
)
