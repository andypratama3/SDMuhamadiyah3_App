package com.sdm3.parent.feature.pembayaran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.security.SecureTokenManager
import com.sdm3.parent.data.remote.dto.PaymentMethodDto
import com.sdm3.parent.data.remote.dto.SnapTokenResponse
import com.sdm3.parent.data.remote.dto.StudentFeeDto
import com.sdm3.parent.domain.repository.PaymentRepositoryContract

data class PilihMetodeBayarUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val selectedFee: StudentFeeDto? = null,
    val snapTokenResponse: SnapTokenResponse? = null,
    val selectedMethod: String? = null,
    val studentFeeId: String = "",
    val snapTokenRequested: Boolean = false,
    val paymentMethods: List<PaymentMethodDto> = emptyList()
) : ScreenState

class PilihMetodeBayarViewModel(
    private val paymentRepository: PaymentRepositoryContract,
    private val secureTokenManager: SecureTokenManager
) : BaseViewModel<PilihMetodeBayarUiState>(PilihMetodeBayarUiState()) {

    fun init(studentFeeId: String) {
        updateState { it.copy(studentFeeId = studentFeeId) }
    }

    fun loadFeeAndMethods() {
        val studentId = secureTokenManager.getSelectedStudentId() ?: return
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            when (val feesResult = paymentRepository.getStudentFees(studentId)) {
                is ApiResult.Success -> {
                    val fee = feesResult.data.find { it.id == uiState.value.studentFeeId }
                    updateState { it.copy(selectedFee = fee) }
                }
                is ApiResult.Error -> {
                    updateState { it.copy(errorMessage = feesResult.error.toUserMessage()) }
                }
            }
            when (val methodsResult = paymentRepository.getPaymentMethods()) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(
                            paymentMethods = methodsResult.data,
                            isLoading = false,
                            isEmpty = methodsResult.data.isEmpty()
                        )
                    }
                }
                is ApiResult.Error -> {
                    updateState {
                        it.copy(isLoading = false, errorMessage = methodsResult.error.toUserMessage())
                    }
                }
            }
        }
    }

    fun loadPaymentMethods() {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            when (val result = paymentRepository.getPaymentMethods()) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(paymentMethods = result.data, isLoading = false)
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

    fun requestSnapToken(studentFeeId: String, method: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null, selectedMethod = method, snapTokenRequested = false) }
            when (val result = paymentRepository.getSnapToken(studentFeeId, method)) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(snapTokenResponse = result.data, isLoading = false, snapTokenRequested = true)
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

    fun selectMethod(method: String) {
        updateState { it.copy(selectedMethod = method) }
    }

    /** Tandai snap token sudah diproses agar tidak memicu navigasi ulang saat kembali. */
    fun consumeSnapToken() {
        updateState { it.copy(snapTokenRequested = false) }
    }

    fun showError(message: String) {
        updateState { it.copy(isLoading = false, errorMessage = message) }
    }
}
