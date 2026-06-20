package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DashboardDto(
    val student: StudentDto,
    val attendanceSummary: AttendanceSummaryDto? = null,
    val recentGrades: List<GradeDto>? = null,
    val activeFees: List<StudentFeeDto>? = null,
    val announcements: List<ArticleDto>? = null
)
