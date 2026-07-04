package com.sdm3.parent.feature.pembayaran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.PaymentDto
import com.sdm3.parent.domain.repository.PaymentRepositoryContract

data class DetailBuktiBayarUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val payment: PaymentDto? = null,
    val isGeneratingReceipt: Boolean = false,
    val receiptUrlToOpen: String? = null,
    val receiptMessage: String? = null
) : ScreenState

class DetailBuktiBayarViewModel(
    private val paymentRepository: PaymentRepositoryContract
) : BaseViewModel<DetailBuktiBayarUiState>(DetailBuktiBayarUiState()) {

    fun loadPaymentDetail(paymentId: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            when (val result = paymentRepository.getPaymentDetail(paymentId)) {
                is ApiResult.Success -> {
                    updateState { it.copy(payment = result.data, isLoading = false) }
                }
                is ApiResult.Error -> {
                    updateState {
                        it.copy(isLoading = false, errorMessage = result.error.toUserMessage())
                    }
                }
            }
        }
    }

    /** Minta backend membuat PDF kwitansi resmi, lalu buka tautannya. */
    fun generateReceipt(paymentId: String) {
        if (uiState.value.isGeneratingReceipt) return
        launchSafely {
            updateState { it.copy(isGeneratingReceipt = true) }
            when (val result = paymentRepository.getReceiptUrl(paymentId)) {
                is ApiResult.Success -> {
                    val url = result.data
                    if (url.isBlank()) {
                        updateState {
                            it.copy(
                                isGeneratingReceipt = false,
                                receiptMessage = "Kwitansi belum tersedia untuk transaksi ini."
                            )
                        }
                    } else {
                        updateState {
                            it.copy(isGeneratingReceipt = false, receiptUrlToOpen = url)
                        }
                    }
                }
                is ApiResult.Error -> {
                    updateState {
                        it.copy(
                            isGeneratingReceipt = false,
                            receiptMessage = result.error.toUserMessage()
                        )
                    }
                }
            }
        }
    }

    fun consumeReceiptUrl() {
        updateState { it.copy(receiptUrlToOpen = null) }
    }

    fun consumeReceiptMessage() {
        updateState { it.copy(receiptMessage = null) }
    }
}
