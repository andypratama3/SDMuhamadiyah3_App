package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RaporInstanceDto(
    val id: String,
    @SerialName("student_id")
    val studentId: String,
    val semester: String,
    @SerialName("semester_label")
    val semesterLabel: String? = null,
    @SerialName("academic_year")
    val academicYear: String? = null,
    val status: String,
    @SerialName("generated_pdf_url")
    val generatedPdfUrl: String? = null,
    @SerialName("verification_code")
    val verificationCode: String? = null,
    @SerialName("document_number")
    val documentNumber: String? = null,
    @SerialName("approved_at")
    val approvedAt: String? = null,
    @SerialName("generated_at")
    val generatedAt: String? = null,
    @SerialName("pdf_url")
    val pdfUrl: String? = null,
    @SerialName("academic_year_obj")
    val academicYearObj: RaporAcademicYearDto? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class RaporAcademicYearDto(
    val id: String,
    val name: String
)

@Serializable
data class RaporDownloadDto(
    val id: String? = null,
    val url: String? = null,
    @SerialName("student_name")
    val studentName: String? = null,
    val semester: String? = null
)

@Serializable
data class RaporVerifyResponse(
    val valid: Boolean,
    val message: String,
    @SerialName("student_name")
    val studentName: String? = null,
    val nisn: String? = null
)
