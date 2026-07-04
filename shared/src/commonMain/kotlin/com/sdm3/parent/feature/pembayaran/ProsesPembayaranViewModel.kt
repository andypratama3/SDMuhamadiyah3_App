package com.sdm3.parent.feature.pembayaran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.PaymentDto
import com.sdm3.parent.domain.repository.PaymentRepositoryContract

enum class PaymentProcessStatus {
    WAITING_PAYMENT, PROCESSING, SUCCESS, FAILED
}

data class ProsesPembayaranUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val paymentId: String = "",
    val orderId: String = "",
    val snapTokenUrl: String = "",
    val redirectUrl: String? = null,
    val vaNumber: String = "",
    val grossAmount: Double = 0.0,
    val paymentMethod: String = "",
    val status: PaymentProcessStatus = PaymentProcessStatus.WAITING_PAYMENT
) : ScreenState

class ProsesPembayaranViewModel(
    private val paymentRepository: PaymentRepositoryContract
) : BaseViewModel<ProsesPembayaranUiState>(ProsesPembayaranUiState()) {

    fun loadPaymentInstructions(paymentId: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null, paymentId = paymentId) }
            when (val result = paymentRepository.getPaymentDetail(paymentId)) {
                is ApiResult.Success -> {
                    val p = result.data
                    updateState {
                        it.copy(
                            isLoading = false,
                            orderId = p.orderId,
                            snapTokenUrl = p.paymentUrl ?: "",
                            redirectUrl = p.paymentUrl,
                            vaNumber = p.vaNumber ?: "",
                            grossAmount = p.grossAmount ?: 0.0,
                            paymentMethod = p.paymentType ?: "",
                            status = mapStatus(p.status)
                        )
                    }
                }
                is ApiResult.Error -> {
                    updateState {
                        it.copy(isLoading = false, errorMessage = result.error.toUserMessage())
                    }
                }
            }
        }
    }

    fun pollStatus() {
        val chargeId = uiState.value.orderId.ifBlank { uiState.value.paymentId }
        if (chargeId.isBlank()) return
        launchSafely {
            updateState { it.copy(isLoading = true, status = PaymentProcessStatus.PROCESSING) }
            when (val result = paymentRepository.checkPaymentStatus(chargeId)) {
                is ApiResult.Success -> {
                    val payment = result.data
                    updateState {
                        it.copy(
                            isLoading = false,
                            vaNumber = payment.vaNumber ?: it.vaNumber,
                            grossAmount = payment.grossAmount ?: it.grossAmount,
                            paymentMethod = payment.paymentType ?: it.paymentMethod,
                            status = mapStatus(payment.status)
                        )
                    }
                }
                is ApiResult.Error -> {
                    updateState {
                        it.copy(isLoading = false, errorMessage = result.error.toUserMessage())
                    }
                }
            }
        }
    }

    /**
     * Polling latar tanpa mengubah [isLoading]/error — dipakai untuk memeriksa
     * status pembayaran secara berkala tanpa membuat layar berkedip.
     */
    fun pollStatusSilently() {
        val chargeId = uiState.value.orderId.ifBlank { uiState.value.paymentId }
        if (chargeId.isBlank()) return
        launchSafely {
            val result = paymentRepository.checkPaymentStatus(chargeId)
            if (result is ApiResult.Success) {
                val payment = result.data
                updateState {
                    it.copy(
                        vaNumber = payment.vaNumber ?: it.vaNumber,
                        grossAmount = payment.grossAmount ?: it.grossAmount,
                        paymentMethod = payment.paymentType ?: it.paymentMethod,
                        status = mapStatus(payment.status)
                    )
                }
            }
        }
    }

    private fun mapStatus(raw: String?): PaymentProcessStatus = when (raw?.lowercase()) {
        "settlement", "success", "capture", "paid", "lunas", "completed" -> PaymentProcessStatus.SUCCESS
        "failed", "failure", "expire", "expired", "deny", "cancel", "cancelled", "refund", "refunded" -> PaymentProcessStatus.FAILED
        else -> PaymentProcessStatus.WAITING_PAYMENT
    }
}
