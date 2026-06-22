package com.sdm3.parent.feature.kehadiran

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.data.remote.dto.AttendanceDto
import com.sdm3.parent.data.remote.dto.AttendanceSummaryDto
import kotlinx.coroutines.delay

data class KehadiranSiswaUiState(
    val studentId: String = "",
    val attendances: List<AttendanceDto> = emptyList(),
    val summary: AttendanceSummaryDto? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = true,
    val selectedMonth: Int = 10,
    val selectedYear: Int = 2023
) : ScreenState

class KehadiranSiswaViewModel : BaseViewModel<KehadiranSiswaUiState>(KehadiranSiswaUiState()) {

    fun loadAttendances(studentId: String, month: Int? = null, year: Int? = null) {
        launchSafely {
            val m = month ?: uiState.value.selectedMonth
            val y = year ?: uiState.value.selectedYear
            updateState { it.copy(isLoading = true, errorMessage = null, studentId = studentId, selectedMonth = m, selectedYear = y) }
            delay(1000)
            
            // Dummy attendances for October 2023
            val dummyAttendances = (1..30).map { day ->
                AttendanceDto(
                    id = "at_$day",
                    date = "2023-10-${day.toString().padStart(2, '0')}",
                    status = if (day == 4) "sakit" else if (day == 30) "izin" else if (listOf(1, 7, 8, 14, 15, 21, 22, 28, 29).contains(day)) "libur" else "hadir"
                )
            }

            updateState { it.copy(isLoading = false, attendances = dummyAttendances, isEmpty = false) }
        }
    }

    fun loadSummary(studentId: String) {
        updateState { it.copy(summary = AttendanceSummaryDto(hadir = 18, sakit = 1, izin = 2, alpa = 0)) }
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
