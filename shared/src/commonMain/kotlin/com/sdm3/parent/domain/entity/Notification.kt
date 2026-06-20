package com.sdm3.parent.domain.entity

data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String
)

data class ReportCard(
    val id: String,
    val studentId: String,
    val academicYear: String,
    val semester: String,
    val status: String,
    val pdfUrl: String?,
    val verificationCode: String?,
    val documentNumber: String?,
    val approvedAt: String?,
    val publishedAt: String?
)
