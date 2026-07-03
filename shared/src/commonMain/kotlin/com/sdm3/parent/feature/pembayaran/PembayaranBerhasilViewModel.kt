package com.sdm3.parent.feature.pembayaran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.domain.repository.PaymentRepositoryContract

data class PembayaranBerhasilUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val transactionId: String = "",
    val paymentTitle: String = "",
    val amount: Long = 0,
    val paymentMethod: String = "",
    val paidAt: String = "",
    val orderId: String = ""
) : ScreenState

class PembayaranBerhasilViewModel(
    private val paymentRepository: PaymentRepositoryContract
) : BaseViewModel<PembayaranBerhasilUiState>(PembayaranBerhasilUiState()) {

    fun loadTransaction(transactionId: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null, transactionId = transactionId) }
            when (val result = paymentRepository.getPaymentDetail(transactionId)) {
                is ApiResult.Success -> {
                    val p = result.data
                    updateState {
                        it.copy(
                            isLoading = false,
                            paymentTitle = p.paymentTitle?.name ?: "",
                            amount = (p.grossAmount?.toLong() ?: 0),
                            paymentMethod = p.paymentType ?: "",
                            paidAt = p.paidAt ?: "",
                            orderId = p.orderId
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
}
