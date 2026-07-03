package com.sdm3.parent.feature.nilai

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.GradeComponentDto
import com.sdm3.parent.domain.repository.GradeRepositoryContract

data class DetailNilaiMapelUiState(
    val studentId: String = "",
    val subjectId: String = "",
    val subjectName: String = "",
    val components: List<GradeComponentDto> = emptyList(),
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = true
) : ScreenState

class DetailNilaiMapelViewModel(
    private val gradeRepository: GradeRepositoryContract
) : BaseViewModel<DetailNilaiMapelUiState>(DetailNilaiMapelUiState()) {

    fun loadComponents(studentId: String, subjectId: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null, studentId = studentId, subjectId = subjectId) }

            when (val result = gradeRepository.getGradeComponents(studentId, subjectId)) {
                is ApiResult.Success -> {
                    val components = result.data
                    val subjectName = components.firstOrNull()?.subjectName ?: ""
                    updateState {
                        it.copy(
                            isLoading = false,
                            components = components,
                            subjectName = subjectName,
                            isEmpty = components.isEmpty()
                        )
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

    fun refresh() {
        val s = uiState.value
        if (s.studentId.isNotEmpty() && s.subjectId.isNotEmpty()) {
            loadComponents(s.studentId, s.subjectId)
        }
    }
}
