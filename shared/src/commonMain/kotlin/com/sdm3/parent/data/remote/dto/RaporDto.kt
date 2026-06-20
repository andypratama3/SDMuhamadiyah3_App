package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RaporInstanceDto(
    val id: String,
    @SerialName("student_id")
    val studentId: String,
    val semester: String,
    @SerialName("academic_year")
    val academicYear: String,
    val status: String,
    @SerialName("pdf_url")
    val pdfUrl: String? = null
)

@Serializable
data class RaporVerifyResponse(
    val valid: Boolean,
    val message: String,
    @SerialName("student_name")
    val studentName: String? = null,
    val nisn: String? = null
)
