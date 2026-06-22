package com.sdm3.parent.feature.pembayaran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.data.remote.dto.PaymentDto
import kotlinx.coroutines.delay

data class DetailBuktiBayarUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val payment: PaymentDto? = null
) : ScreenState

class DetailBuktiBayarViewModel : BaseViewModel<DetailBuktiBayarUiState>(DetailBuktiBayarUiState()) {

    fun loadPaymentDetail(paymentId: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            delay(1000)
            val dummyPayment = PaymentDto(
                id = paymentId,
                orderId = "SDM3-77291044",
                grossAmount = 702500.0,
                status = "success",
                paymentType = "bank_transfer",
                vaNumber = "8507 0812 3456 7890",
                paidAt = "15 Juli 2024, 08:42",
                createdAt = "15 Juli 2024, 08:30"
            )
            updateState { it.copy(payment = dummyPayment, isLoading = false) }
        }
    }
}
