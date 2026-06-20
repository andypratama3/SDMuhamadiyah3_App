package com.sdm3.parent.domain.entity

data class Payment(
    val id: String,
    val orderId: String,
    val studentId: String,
    val paymentTitleName: String?,
    val grossAmount: Long,
    val status: PaymentStatus,
    val paymentType: String?,
    val vaNumber: String?,
    val paymentUrl: String?,
    val transactionId: String?,
    val paidAt: String?,
    val createdAt: String
)

enum class PaymentStatus { PENDING, SUCCESS, FAILED, EXPIRED, CANCELLED }

data class StudentFee(
    val id: String,
    val studentId: String,
    val paymentTitleId: String,
    val paymentTitleName: String,
    val amount: Long,
    val dueDate: String?,
    val status: FeeStatus,
    val academicYear: String,
    val notes: String?
)

enum class FeeStatus { BELUM_BAYAR, SEBAGIAN, LUNAS, DIBEBASKAN }
