package com.sdm3.parent.feature.guru

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.TeacherRosterStudentDto
import com.sdm3.parent.domain.repository.TeacherAttendanceRepositoryContract
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class GuruAbsensiUiState(
    val classroomId: String = "",
    val classroomName: String = "",
    val date: String = todayIso(),
    val students: List<TeacherRosterStudentDto> = emptyList(),
    val originalStatuses: Map<String, String?> = emptyMap(),
    val selectedStatuses: Map<String, String> = emptyMap(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = true,
) : ScreenState

private fun todayIso(): String {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    return today.toString()
}

class GuruAbsensiViewModel(
    private val repository: TeacherAttendanceRepositoryContract,
) : BaseViewModel<GuruAbsensiUiState>(GuruAbsensiUiState()) {

    fun init(classroomId: String, classroomName: String) {
        updateState {
            it.copy(classroomId = classroomId, classroomName = classroomName, saveSuccess = false)
        }
        loadRoster()
    }

    fun loadRoster() {
        val state = uiState.value
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null, saveSuccess = false) }
            when (val result = repository.getRoster(state.classroomId, state.date)) {
                is ApiResult.Success -> {
                    val original = result.data.students.associate { it.studentId to it.status }
                    updateState {
                        it.copy(
                            students = result.data.students,
                            originalStatuses = original,
                            selectedStatuses = emptyMap(),
                            isLoading = false,
                            isEmpty = result.data.students.isEmpty(),
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

    fun setStatus(studentId: String, status: String) {
        updateState {
            it.copy(
                selectedStatuses = it.selectedStatuses + (studentId to status),
                saveSuccess = false,
            )
        }
    }

    fun save() {
        val state = uiState.value
        val changedRecords = state.selectedStatuses.filter { (studentId, status) ->
            state.originalStatuses[studentId] != status
        }
        if (changedRecords.isEmpty()) return
        launchSafely {
            updateState { it.copy(isSaving = true, errorMessage = null) }
            when (
                val result = repository.saveAttendance(
                    classroomId = state.classroomId,
                    date = state.date,
                    records = changedRecords,
                )
            ) {
                is ApiResult.Success -> {
                    updateState { it.copy(isSaving = false, saveSuccess = true) }
                    loadRoster()
                }
                is ApiResult.Error -> {
                    updateState {
                        it.copy(isSaving = false, errorMessage = result.error.toUserMessage())
                    }
                }
            }
        }
    }
}
