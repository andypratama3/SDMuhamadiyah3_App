package com.sdm3.parent.feature.nilai

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.GradeDto
import com.sdm3.parent.data.repository.GradeRepository

data class NilaiRaporUiState(
    val studentId: String = "",
    val grades: List<GradeDto> = emptyList(),
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = true,
    val selectedTab: Int = 0,
    val semester: String = "ganjil"
) : ScreenState

class NilaiRaporViewModel(
    private val gradeRepository: GradeRepository
) : BaseViewModel<NilaiRaporUiState>(NilaiRaporUiState()) {

    fun loadGrades(studentId: String, semester: String? = null) {
        launchSafely(
            onError = { error ->
                updateState { it.copy(isLoading = false, errorMessage = error.message ?: "Terjadi kesalahan") }
            }
        ) {
            val currentState = uiState.value
            val sem = semester ?: currentState.semester
            updateState {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    studentId = studentId,
                    semester = sem
                )
            }
            when (val result = gradeRepository.getGrades(studentId, sem)) {
                is ApiResult.Success -> {
                    val grades = result.data
                    updateState { it.copy(isLoading = false, grades = grades, isEmpty = grades.isEmpty()) }
                }
                is ApiResult.Error -> {
                    updateState { it.copy(isLoading = false, errorMessage = result.error.toUserMessage()) }
                }
            }
        }
    }

    fun selectTab(index: Int) {
        updateState { it.copy(selectedTab = index) }
    }

    fun refresh() {
        val s = uiState.value
        loadGrades(s.studentId, s.semester)
    }
}
