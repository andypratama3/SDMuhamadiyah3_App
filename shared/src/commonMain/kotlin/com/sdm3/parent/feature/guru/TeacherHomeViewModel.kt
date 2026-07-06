package com.sdm3.parent.feature.guru

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.TeacherClassroomDto
import com.sdm3.parent.domain.repository.AuthRepositoryContract
import com.sdm3.parent.domain.repository.TeacherAttendanceRepositoryContract

data class TeacherHomeUiState(
    val teacherName: String = "",
    val classrooms: List<TeacherClassroomDto> = emptyList(),
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = true,
) : ScreenState

class TeacherHomeViewModel(
    private val teacherRepository: TeacherAttendanceRepositoryContract,
    private val authRepository: AuthRepositoryContract,
) : BaseViewModel<TeacherHomeUiState>(TeacherHomeUiState()) {

    fun load() {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            when (val userResult = authRepository.getAuthenticatedUser()) {
                is ApiResult.Success -> {
                    updateState { it.copy(teacherName = userResult.data.name) }
                }
                is ApiResult.Error -> {
                    updateState {
                        it.copy(isLoading = false, errorMessage = userResult.error.toUserMessage())
                    }
                    return@launchSafely
                }
            }
            when (val result = teacherRepository.getClassrooms()) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(
                            classrooms = result.data,
                            isLoading = false,
                            isEmpty = result.data.isEmpty(),
                            errorMessage = null,
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
}
