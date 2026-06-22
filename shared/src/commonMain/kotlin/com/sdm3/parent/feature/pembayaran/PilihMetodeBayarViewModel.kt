package com.sdm3.parent.feature.pembayaran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.data.remote.dto.SnapTokenResponse
import com.sdm3.parent.data.remote.dto.StudentFeeDto
import kotlinx.coroutines.delay

data class PilihMetodeBayarUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val selectedFee: StudentFeeDto? = null,
    val snapTokenResponse: SnapTokenResponse? = null,
    val selectedMethod: String? = null
) : ScreenState

class PilihMetodeBayarViewModel : BaseViewModel<PilihMetodeBayarUiState>(PilihMetodeBayarUiState()) {

    fun requestSnapToken(paymentId: String, method: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null, selectedMethod = method) }
            delay(1000)
            updateState {
                it.copy(
                    snapTokenResponse = SnapTokenResponse(snapToken = "dummy_token", redirectUrl = "https://example.com"),
                    isLoading = false
                )
            }
        }
    }

    fun selectMethod(method: String) {
        updateState { it.copy(selectedMethod = method) }
    }
}
