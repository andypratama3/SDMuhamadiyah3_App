package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TranscriptDto(
    @SerialName("student_id")
    val studentId: String,
    val semesters: List<SemesterTranscriptDto>
)

@Serializable
data class SemesterTranscriptDto(
    val semester: String,
    val subjects: List<GradeDto>,
    val average: Double? = null
)
