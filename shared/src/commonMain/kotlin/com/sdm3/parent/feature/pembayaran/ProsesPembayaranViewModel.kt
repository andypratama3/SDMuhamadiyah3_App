package com.sdm3.parent.feature.pembayaran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import kotlinx.coroutines.delay

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

class ProsesPembayaranViewModel : BaseViewModel<ProsesPembayaranUiState>(ProsesPembayaranUiState()) {

    fun startPayment(snapTokenUrl: String, redirectUrl: String? = null, vaNumber: String = "", grossAmount: Double = 0.0, paymentMethod: String = "") {
        updateState {
            it.copy(
                snapTokenUrl = snapTokenUrl,
                redirectUrl = redirectUrl,
                vaNumber = "8507 0812 3456 7890",
                grossAmount = 1250000.0,
                paymentMethod = "Bank Syariah Indonesia",
                status = PaymentProcessStatus.WAITING_PAYMENT
            )
        }
    }

    fun pollStatus(chargeId: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, status = PaymentProcessStatus.PROCESSING) }
            delay(2000)
            updateState { it.copy(isLoading = false, status = PaymentProcessStatus.SUCCESS) }
        }
    }
}
