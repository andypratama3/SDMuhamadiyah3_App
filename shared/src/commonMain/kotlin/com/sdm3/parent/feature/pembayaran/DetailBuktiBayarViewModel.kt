package com.sdm3.parent.feature.pembayaran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.PaymentDto
import com.sdm3.parent.data.repository.PaymentRepository

data class DetailBuktiBayarUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val payment: PaymentDto? = null
) : ScreenState

class DetailBuktiBayarViewModel(
    private val paymentRepository: PaymentRepository
) : BaseViewModel<DetailBuktiBayarUiState>(DetailBuktiBayarUiState()) {

    fun loadPaymentDetail(paymentId: String) {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        launchSafely {
            when (val result = paymentRepository.getPayments("")) {
                is ApiResult.Success -> {
                    val payment = result.data.find { it.id == paymentId }
                    updateState {
                        it.copy(
                            payment = payment,
                            isLoading = false,
                            isEmpty = payment == null
                        )
                    }
                }
                is ApiResult.Error -> {
                    updateState { it.copy(isLoading = false, errorMessage = result.error.toUserMessage()) }
                }
            }
        }
    }
}
