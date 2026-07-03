package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentFeeDto(
    val id: String,
    @SerialName("payment_title_id")
    val paymentTitleId: String,
    @SerialName("payment_title_name")
    val paymentTitleName: String,
    val amount: Double,
    @SerialName("due_date")
    val dueDate: String? = null,
    val status: String
)

@Serializable
data class PaymentDto(
    val id: String,
    @SerialName("order_id")
    val orderId: String,
    @SerialName("gross_amount")
    val grossAmount: Double? = null,
    @SerialName("payment_type")
    val paymentType: String? = null,
    val status: String,
    @SerialName("payment_url")
    val paymentUrl: String? = null,
    @SerialName("va_number")
    val vaNumber: String? = null,
    @SerialName("student_name")
    val studentName: String? = null,
    @SerialName("student_class")
    val studentClass: String? = null,
    @SerialName("student_nisn")
    val studentNisn: String? = null,
    @SerialName("payment_title")
    val paymentTitle: PaymentTitleDto? = null,
    @SerialName("paid_at")
    val paidAt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class PaymentTitleDto(
    val id: String,
    val name: String
)

@Serializable
data class SnapTokenResponse(
    @SerialName("snap_token")
    val snapToken: String,
    @SerialName("redirect_url")
    val redirectUrl: String? = null,
    @SerialName("payment_id")
    val paymentId: String? = null,
    @SerialName("order_id")
    val orderId: String? = null
)

@Serializable
data class SnapTokenRequest(
    @SerialName("payment_method")
    val paymentMethod: String
)

@Serializable
data class PaymentMethodDto(
    val id: String,
    val name: String,
    val description: String? = null
)
