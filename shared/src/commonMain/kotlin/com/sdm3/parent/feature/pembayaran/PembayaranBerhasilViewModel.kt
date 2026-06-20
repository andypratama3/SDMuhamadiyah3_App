package com.sdm3.parent.feature.pembayaran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState

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

class PembayaranBerhasilViewModel : BaseViewModel<PembayaranBerhasilUiState>(PembayaranBerhasilUiState()) {

    fun loadTransaction(transactionId: String) {
        updateState { it.copy(isLoading = true, transactionId = transactionId) }
        // Transaction details would be loaded from a repository if available
        updateState { it.copy(isLoading = false) }
    }
}
