package com.sdm3.parent.feature.profil

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.StudentDto
import com.sdm3.parent.data.repository.ProfileRepository
import com.sdm3.parent.data.repository.StudentRepository

data class ProfilAkunUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val students: List<StudentDto> = emptyList(),
    val isEditing: Boolean = false,
    val editedName: String = "",
    val editedPhone: String = ""
) : ScreenState

class ProfilAkunViewModel(
    private val profileRepository: ProfileRepository,
    private val studentRepository: StudentRepository
) : BaseViewModel<ProfilAkunUiState>(ProfilAkunUiState()) {

    fun loadProfile() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        launchSafely {
            when (val result = profileRepository.getProfile()) {
                is ApiResult.Success -> {
                    val dto = result.data
                    updateState {
                        it.copy(
                            name = dto.name,
                            phone = dto.phone.orEmpty(),
                            email = dto.email,
                            editedName = dto.name,
                            editedPhone = dto.phone.orEmpty(),
                            isLoading = false
                        )
                    }
                }
                is ApiResult.Error -> {
                    updateState { it.copy(isLoading = false, errorMessage = result.error.toUserMessage()) }
                }
            }
        }
    }

    fun loadStudents() {
        launchSafely {
            when (val result = studentRepository.getStudents()) {
                is ApiResult.Success -> {
                    updateState { it.copy(students = result.data) }
                }
                is ApiResult.Error -> { }
            }
        }
    }

    fun startEdit() {
        val s = uiState.value
        updateState { it.copy(isEditing = true, editedName = s.name, editedPhone = s.phone) }
    }

    fun cancelEdit() {
        val s = uiState.value
        updateState { it.copy(isEditing = false, editedName = s.name, editedPhone = s.phone) }
    }

    fun updateEditedName(name: String) {
        updateState { it.copy(editedName = name) }
    }

    fun updateEditedPhone(phone: String) {
        updateState { it.copy(editedPhone = phone) }
    }

    fun updateProfile() {
        val s = uiState.value
        updateState { it.copy(isLoading = true, errorMessage = null) }
        launchSafely {
            when (val result = profileRepository.updateProfile(s.editedName, s.editedPhone)) {
                is ApiResult.Success -> {
                    val dto = result.data
                    updateState {
                        it.copy(
                            name = dto.name,
                            phone = dto.phone.orEmpty(),
                            email = dto.email,
                            isEditing = false,
                            isLoading = false
                        )
                    }
                }
                is ApiResult.Error -> {
                    updateState { it.copy(isLoading = false, errorMessage = result.error.toUserMessage()) }
                }
            }
        }
    }

    fun refresh() {
        loadProfile()
        loadStudents()
    }
}
