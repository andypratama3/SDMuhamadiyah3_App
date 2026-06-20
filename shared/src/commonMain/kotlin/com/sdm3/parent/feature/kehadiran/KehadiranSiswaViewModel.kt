package com.sdm3.parent.feature.kehadiran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.AttendanceDto
import com.sdm3.parent.data.remote.dto.AttendanceSummaryDto
import com.sdm3.parent.data.repository.AttendanceRepository

data class KehadiranSiswaUiState(
    val studentId: String = "",
    val attendances: List<AttendanceDto> = emptyList(),
    val summary: AttendanceSummaryDto? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = true,
    val selectedMonth: Int = 1,
    val selectedYear: Int = 2026
) : ScreenState

class KehadiranSiswaViewModel(
    private val attendanceRepository: AttendanceRepository
) : BaseViewModel<KehadiranSiswaUiState>(KehadiranSiswaUiState()) {

    fun loadAttendances(studentId: String, month: Int? = null, year: Int? = null) {
        launchSafely(
            onError = { error ->
                updateState { it.copy(isLoading = false, errorMessage = error.message ?: "Terjadi kesalahan") }
            }
        ) {
            val currentState = uiState.value
            val m = month ?: currentState.selectedMonth
            val y = year ?: currentState.selectedYear
            updateState {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    studentId = studentId,
                    selectedMonth = m,
                    selectedYear = y
                )
            }
            when (val result = attendanceRepository.getAttendances(studentId, m, y)) {
                is ApiResult.Success -> {
                    val attendances = result.data
                    updateState { it.copy(isLoading = false, attendances = attendances, isEmpty = attendances.isEmpty()) }
                }
                is ApiResult.Error -> {
                    updateState { it.copy(isLoading = false, errorMessage = result.error.toUserMessage()) }
                }
            }
        }
    }

    fun loadSummary(studentId: String) {
        launchSafely(
            onError = { error ->
                updateState { it.copy(isLoading = false, errorMessage = error.message ?: "Terjadi kesalahan") }
            }
        ) {
            when (val result = attendanceRepository.getAttendanceSummary(studentId)) {
                is ApiResult.Success -> {
                    updateState { it.copy(summary = result.data) }
                }
                is ApiResult.Error -> {
                    updateState { it.copy(errorMessage = result.error.toUserMessage()) }
                }
            }
        }
    }

    fun changeMonth(month: Int, year: Int) {
        val currentState = uiState.value
        loadAttendances(currentState.studentId, month, year)
    }

    fun refresh() {
        val s = uiState.value
        if (s.studentId.isNotEmpty()) {
            loadAttendances(s.studentId, s.selectedMonth, s.selectedYear)
        }
    }
}
