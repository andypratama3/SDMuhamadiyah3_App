package com.sdm3.parent.feature.home

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.ArticleDto
import com.sdm3.parent.data.remote.dto.AttendanceSummaryDto
import com.sdm3.parent.data.remote.dto.DashboardDto
import com.sdm3.parent.data.remote.dto.GradeDto
import com.sdm3.parent.data.remote.dto.StudentFeeDto
import com.sdm3.parent.data.repository.DashboardRepository

data class HomeUiState(
    val studentName: String = "",
    val className: String = "",
    val attendanceSummary: AttendanceSummaryDto? = null,
    val recentGrades: List<GradeDto> = emptyList(),
    val activeFees: List<StudentFeeDto> = emptyList(),
    val announcements: List<ArticleDto> = emptyList(),
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = true
) : ScreenState

class HomeViewModel(
    private val dashboardRepository: DashboardRepository
) : BaseViewModel<HomeUiState>(HomeUiState()) {

    fun loadDashboard(studentId: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            when (val result = dashboardRepository.getDashboard(studentId)) {
                is ApiResult.Success -> {
                    val data: DashboardDto = result.data
                    updateState {
                        it.copy(
                            isLoading = false,
                            studentName = data.student.name,
                            className = data.student.className.orEmpty(),
                            attendanceSummary = data.attendanceSummary,
                            recentGrades = data.recentGrades.orEmpty(),
                            activeFees = data.activeFees.orEmpty(),
                            announcements = data.announcements.orEmpty(),
                            isEmpty = false
                        )
                    }
                }
                is ApiResult.Error -> {
                    updateState { it.copy(isLoading = false, errorMessage = result.error.toUserMessage()) }
                }
            }
        }
    }

    fun refresh(studentId: String) {
        loadDashboard(studentId)
    }
}
