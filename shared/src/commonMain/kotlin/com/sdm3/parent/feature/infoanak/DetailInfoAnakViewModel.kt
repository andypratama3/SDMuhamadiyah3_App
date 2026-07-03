package com.sdm3.parent.feature.infoanak

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.StudentDto
import com.sdm3.parent.domain.repository.StudentRepositoryContract

data class DetailInfoAnakUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val student: StudentDto? = null
) : ScreenState

class DetailInfoAnakViewModel(
    private val studentRepository: StudentRepositoryContract
) : BaseViewModel<DetailInfoAnakUiState>(DetailInfoAnakUiState()) {

    fun loadStudentDetail(id: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
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
                    updateState {
                        it.copy(isLoading = false, errorMessage = result.error.toUserMessage())
                    }
                }
            }
        }
    }

    fun refresh(studentId: String) {
        loadStudentDetail(studentId)
    }
}
