package com.sdm3.parent.feature.pembayaran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.PaymentDto
import com.sdm3.parent.data.remote.dto.StudentFeeDto
import com.sdm3.parent.domain.repository.PaymentRepositoryContract

data class PembayaranSppUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val studentId: String = "",
    val fees: List<StudentFeeDto> = emptyList(),
    val payments: List<PaymentDto> = emptyList(),
    val studentName: String = ""
) : ScreenState

class PembayaranSppViewModel(
    private val paymentRepository: PaymentRepositoryContract
) : BaseViewModel<PembayaranSppUiState>(PembayaranSppUiState()) {

    fun loadData(studentId: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null, studentId = studentId) }

            val feesResult = paymentRepository.getStudentFees(studentId)
            val paymentsResult = paymentRepository.getPayments(studentId)

            val fees = if (feesResult is ApiResult.Success) feesResult.data else emptyList()
            val payments = if (paymentsResult is ApiResult.Success) paymentsResult.data else emptyList()
            val error = if (feesResult is ApiResult.Error) feesResult.error.toUserMessage()
                else if (paymentsResult is ApiResult.Error) paymentsResult.error.toUserMessage()
                else null

            updateState {
                it.copy(
                    isLoading = false,
                    studentName = "",
                    fees = fees,
                    payments = payments,
                    isEmpty = fees.isEmpty() && payments.isEmpty(),
                    errorMessage = error
                )
            }
        }
    }

    fun refresh() {
        val currentId = uiState.value.studentId
        if (currentId.isNotEmpty()) loadData(currentId)
    }
}
