package com.sdm3.parent.feature.pembayaran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.repository.PaymentRepository

enum class PaymentProcessStatus {
    WAITING_PAYMENT, PROCESSING, SUCCESS, FAILED
}

data class ProsesPembayaranUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val snapTokenUrl: String = "",
    val redirectUrl: String? = null,
    val vaNumber: String = "",
    val grossAmount: Double = 0.0,
    val paymentMethod: String = "",
    val status: PaymentProcessStatus = PaymentProcessStatus.WAITING_PAYMENT
) : ScreenState

class ProsesPembayaranViewModel(
    private val paymentRepository: PaymentRepository
) : BaseViewModel<ProsesPembayaranUiState>(ProsesPembayaranUiState()) {

    fun startPayment(snapTokenUrl: String, redirectUrl: String? = null, vaNumber: String = "", grossAmount: Double = 0.0, paymentMethod: String = "") {
        updateState {
            it.copy(
                snapTokenUrl = snapTokenUrl,
                redirectUrl = redirectUrl,
                vaNumber = vaNumber,
                grossAmount = grossAmount,
                paymentMethod = paymentMethod,
                status = PaymentProcessStatus.WAITING_PAYMENT
            )
        }
    }

    fun pollStatus(chargeId: String) {
        updateState { it.copy(isLoading = true, status = PaymentProcessStatus.PROCESSING) }
        launchSafely {
            when (val result = paymentRepository.checkPaymentStatus(chargeId)) {
                is ApiResult.Success -> {
                    val newStatus = when (result.data.status.uppercase()) {
                        "SUCCESS", "SETTLEMENT" -> PaymentProcessStatus.SUCCESS
                        "FAILED", "EXPIRED", "DENY" -> PaymentProcessStatus.FAILED
                        else -> PaymentProcessStatus.PROCESSING
                    }
                    updateState { it.copy(isLoading = false, status = newStatus) }
                }
                is ApiResult.Error -> {
                    updateState { it.copy(isLoading = false, errorMessage = result.error.toUserMessage()) }
                }
            }
        }
    }
}
