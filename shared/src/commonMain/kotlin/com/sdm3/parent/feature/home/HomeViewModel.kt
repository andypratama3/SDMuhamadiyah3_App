package com.sdm3.parent.feature.home

import com.sdm3.parent.core.base.MviViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.*
import com.sdm3.parent.data.repository.DashboardRepository

data class HomeUiState(
    val studentId: String = "",
    val studentName: String = "",
    val className: String = "",
    val students: List<StudentDto> = emptyList(),
    val attendanceSummary: AttendanceSummaryDto? = null,
    val recentGrades: List<GradeDto> = emptyList(),
    val activeFees: List<StudentFeeDto> = emptyList(),
    val announcements: List<ArticleDto> = emptyList(),
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = true,
) : ScreenState

sealed interface HomeIntent {
    data class LoadDashboard(val studentId: String) : HomeIntent
    data class Refresh(val studentId: String) : HomeIntent
}

sealed interface HomeEffect {
    data object NavigateToLogin : HomeEffect
}

class HomeViewModel(
    private val repository: DashboardRepository
) : MviViewModel<HomeUiState, HomeIntent, HomeEffect>(HomeUiState()) {

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadDashboard -> loadDashboard(intent.studentId)
            is HomeIntent.Refresh -> loadDashboard(intent.studentId)
        }
    }

    private fun loadDashboard(studentId: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null, studentId = studentId) }

            when (val result = repository.getDashboard(studentId)) {
                is ApiResult.Success -> {
                    val data = result.data
                    updateState {
                        it.copy(
                            isLoading = false,
                            studentId = data.student.id,
                            studentName = data.student.name,
                            className = data.student.className.orEmpty(),
                            attendanceSummary = data.attendanceSummary,
                            recentGrades = data.recentGrades ?: emptyList(),
                            activeFees = data.activeFees ?: emptyList(),
                            announcements = data.announcements ?: emptyList(),
                            isEmpty = false
                        )
                    }
                }
                is ApiResult.Error -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.error.toUserMessage(),
                            isEmpty = it.isEmpty
                        )
                    }
                }
            }
        }
    }
}
