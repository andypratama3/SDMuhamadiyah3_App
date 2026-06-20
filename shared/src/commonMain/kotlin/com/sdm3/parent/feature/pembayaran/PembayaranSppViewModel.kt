package com.sdm3.parent.feature.pembayaran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.PaymentDto
import com.sdm3.parent.data.remote.dto.StudentFeeDto
import com.sdm3.parent.data.repository.PaymentRepository
import com.sdm3.parent.data.repository.StudentRepository

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
    private val paymentRepository: PaymentRepository,
    private val studentRepository: StudentRepository
) : BaseViewModel<PembayaranSppUiState>(PembayaranSppUiState()) {

    fun loadData(studentId: String) {
        updateState { it.copy(isLoading = true, errorMessage = null, studentId = studentId) }
        launchSafely {
            when (val result = paymentRepository.getStudentFees(studentId)) {
                is ApiResult.Success -> {
                    updateState { it.copy(fees = result.data, isLoading = false, isEmpty = result.data.isEmpty()) }
                }
                is ApiResult.Error -> {
                    updateState { it.copy(isLoading = false, errorMessage = result.error.toUserMessage()) }
                }
            }
        }
        launchSafely {
            when (val result = studentRepository.getStudents()) {
                is ApiResult.Success -> {
                    val student = result.data.find { it.id == studentId }
                    updateState { it.copy(studentName = student?.name ?: "") }
                }
                is ApiResult.Error -> { }
            }
        }
        launchSafely {
            when (val result = paymentRepository.getPayments(studentId)) {
                is ApiResult.Success -> {
                    updateState { it.copy(payments = result.data) }
                }
                is ApiResult.Error -> { }
            }
        }
    }

    fun refresh() {
        val currentId = uiState.value.studentId
        if (currentId.isNotEmpty()) loadData(currentId)
    }
}
