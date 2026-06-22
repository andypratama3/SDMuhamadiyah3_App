package com.sdm3.parent.feature.pembayaran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.data.remote.dto.PaymentDto
import com.sdm3.parent.data.remote.dto.StudentFeeDto
import kotlinx.coroutines.delay

data class PembayaranSppUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val studentId: String = "",
    val fees: List<StudentFeeDto> = emptyList(),
    val payments: List<PaymentDto> = emptyList(),
    val studentName: String = ""
) : ScreenState

class PembayaranSppViewModel : BaseViewModel<PembayaranSppUiState>(PembayaranSppUiState()) {

    fun loadData(studentId: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null, studentId = studentId) }
            delay(1000)
            val dummyFees = listOf(
                StudentFeeDto(id = "f1", paymentTitleId = "p1", paymentTitleName = "SPP Juli 2026", amount = 350000.0, dueDate = "15 Juli 2026", status = "belum_bayar")
            )
            val dummyPayments = listOf(
                PaymentDto(id = "1", orderId = "ORD-001", grossAmount = 350000.0, status = "success", createdAt = "2024-06-12", paidAt = "2024-06-12", paymentType = "bank_transfer"),
                PaymentDto(id = "2", orderId = "ORD-002", grossAmount = 350000.0, status = "success", createdAt = "2024-05-10", paidAt = "2024-05-10", paymentType = "bank_transfer")
            )
            updateState { 
                it.copy(
                    isLoading = false,
                    studentName = "Aisyah Humaira",
                    fees = dummyFees,
                    payments = dummyPayments,
                    isEmpty = false
                )
            }
        }
    }

    fun refresh() {
        val currentId = uiState.value.studentId
        if (currentId.isNotEmpty()) loadData(currentId)
    }
}
