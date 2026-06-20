package com.sdm3.parent.feature.pembayaran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.SnapTokenResponse
import com.sdm3.parent.data.remote.dto.StudentFeeDto
import com.sdm3.parent.data.repository.PaymentRepository

data class PilihMetodeBayarUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val selectedFee: StudentFeeDto? = null,
    val snapTokenResponse: SnapTokenResponse? = null,
    val selectedMethod: String? = null
) : ScreenState

class PilihMetodeBayarViewModel(
    private val paymentRepository: PaymentRepository
) : BaseViewModel<PilihMetodeBayarUiState>(PilihMetodeBayarUiState()) {

    fun requestSnapToken(paymentId: String, method: String) {
        updateState { it.copy(isLoading = true, errorMessage = null, selectedMethod = method) }
        launchSafely {
            when (val result = paymentRepository.getSnapToken(paymentId)) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(
                            snapTokenResponse = result.data,
                            isLoading = false
                        )
                    }
                }
                is ApiResult.Error -> {
                    updateState { it.copy(isLoading = false, errorMessage = result.error.toUserMessage()) }
                }
            }
        }
    }

    fun selectMethod(method: String) {
        updateState { it.copy(selectedMethod = method) }
    }
}
