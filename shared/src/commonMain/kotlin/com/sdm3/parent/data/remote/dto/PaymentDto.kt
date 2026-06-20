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
    @SerialName("va_number")
    val vaNumber: String? = null,
    @SerialName("paid_at")
    val paidAt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class SnapTokenResponse(
    @SerialName("snap_token")
    val snapToken: String,
    @SerialName("redirect_url")
    val redirectUrl: String? = null
)
