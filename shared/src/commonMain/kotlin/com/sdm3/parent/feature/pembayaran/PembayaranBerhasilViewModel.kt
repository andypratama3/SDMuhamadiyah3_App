package com.sdm3.parent.feature.pembayaran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import kotlinx.coroutines.delay

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
        launchSafely {
            updateState { it.copy(isLoading = true, transactionId = transactionId) }
            delay(500)
            updateState { 
                it.copy(
                    isLoading = false,
                    paymentTitle = "SPP Juli 2026",
                    amount = 350000L,
                    paymentMethod = "BCA Virtual Account",
                    paidAt = "20 Juni 2026, 14:30 WIB",
                    orderId = "SDM3-TRX-2026-620-001"
                ) 
            }
        }
    }
}
