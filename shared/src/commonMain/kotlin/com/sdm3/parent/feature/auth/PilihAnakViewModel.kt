package com.sdm3.parent.feature.auth

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.StudentDto
import com.sdm3.parent.domain.repository.StudentRepositoryContract

data class PilihAnakUiState(
    val students: List<StudentDto> = emptyList(),
    val selectedStudentId: String? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = true
) : ScreenState

class PilihAnakViewModel(
    private val studentRepository: StudentRepositoryContract
) : BaseViewModel<PilihAnakUiState>(PilihAnakUiState()) {

    init {
        loadStudents()
    }

    fun loadStudents() {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            when (val result = studentRepository.getStudents()) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            students = result.data,
                            isEmpty = result.data.isEmpty()
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

    fun selectStudent(studentId: String) {
        updateState { it.copy(selectedStudentId = studentId) }
    }
}
