package com.sdm3.parent.feature.kehadiran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.AttendanceDto
import com.sdm3.parent.data.remote.dto.AttendanceSummaryDto
import com.sdm3.parent.domain.repository.AttendanceRepositoryContract
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

private val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

@Suppress("DEPRECATION")
private val currentMonthNumber: Int = today.monthNumber
private val currentYearNumber: Int = today.year

data class KehadiranSiswaUiState(
    val studentId: String = "",
    val attendances: List<AttendanceDto> = emptyList(),
    val summary: AttendanceSummaryDto? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = true,
    val selectedMonth: Int = currentMonthNumber,
    val selectedYear: Int = currentYearNumber
) : ScreenState

class KehadiranSiswaViewModel(
    private val attendanceRepository: AttendanceRepositoryContract
) : BaseViewModel<KehadiranSiswaUiState>(KehadiranSiswaUiState()) {

    fun loadAttendances(studentId: String, month: Int? = null, year: Int? = null) {
        launchSafely {
            val m = month ?: uiState.value.selectedMonth
            val y = year ?: uiState.value.selectedYear
            updateState { it.copy(isLoading = true, errorMessage = null, studentId = studentId, selectedMonth = m, selectedYear = y) }

            when (val result = attendanceRepository.getAttendances(studentId, m, y)) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(isLoading = false, attendances = result.data, isEmpty = result.data.isEmpty())
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

    fun loadSummary(studentId: String) {
        launchSafely {
            // Summary bersifat pelengkap; kegagalan di sini tidak boleh
            // menutupi data kehadiran yang sudah berhasil dimuat.
            val result = attendanceRepository.getAttendanceSummary(studentId)
            if (result is ApiResult.Success) {
                updateState { it.copy(summary = result.data) }
            }
        }
    }

    fun changeMonth(month: Int, year: Int) {
        loadAttendances(uiState.value.studentId, month, year)
    }

    fun refresh() {
        val s = uiState.value
        if (s.studentId.isNotEmpty()) {
            loadAttendances(s.studentId, s.selectedMonth, s.selectedYear)
            loadSummary(s.studentId)
        }
    }
}
