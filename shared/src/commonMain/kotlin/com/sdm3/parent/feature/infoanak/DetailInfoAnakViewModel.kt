package com.sdm3.parent.feature.infoanak

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.ExtracurricularDto
import com.sdm3.parent.data.remote.dto.StudentDto
import com.sdm3.parent.data.repository.ExtracurricularRepository
import com.sdm3.parent.data.repository.StudentRepository

data class DetailInfoAnakUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val student: StudentDto? = null,
    val extracurriculars: List<ExtracurricularDto> = emptyList()
) : ScreenState

class DetailInfoAnakViewModel(
    private val studentRepository: StudentRepository,
    private val extracurricularRepository: ExtracurricularRepository
) : BaseViewModel<DetailInfoAnakUiState>(DetailInfoAnakUiState()) {

    fun loadStudentDetail(id: String) {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        launchSafely {
            when (val result = studentRepository.getStudentDetail(id)) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(
                            student = result.data,
                            isLoading = false,
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

    fun loadExtracurriculars(studentId: String) {
        launchSafely {
            when (val result = extracurricularRepository.getExtracurriculars(studentId)) {
                is ApiResult.Success -> {
                    updateState { it.copy(extracurriculars = result.data) }
                }
                is ApiResult.Error -> { }
            }
        }
    }

    fun refresh(studentId: String) {
        loadStudentDetail(studentId)
        loadExtracurriculars(studentId)
    }
}
