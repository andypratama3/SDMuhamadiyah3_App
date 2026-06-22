package com.sdm3.parent.feature.profil

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.data.remote.dto.StudentDto
import kotlinx.coroutines.delay

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

class ProfilAkunViewModel : BaseViewModel<ProfilAkunUiState>(ProfilAkunUiState()) {

    fun loadProfile() {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            delay(800)
            updateState {
                it.copy(
                    name = "Andy Pratama",
                    phone = "081234567890",
                    email = "andy.pratama@example.com",
                    editedName = "Andy Pratama",
                    editedPhone = "081234567890",
                    isLoading = false
                )
            }
        }
    }

    fun loadStudents() {
        val dummyStudents = listOf(
            StudentDto(
                id = "student_1",
                name = "Ahmad Fathan",
                nisn = "0012345678",
                gender = "Laki-laki",
                className = "4-A (Ibnu Sina)",
                birthPlace = "Samarinda",
                birthDate = "2015-01-15"
            ),
            StudentDto(
                id = "student_2",
                name = "Zahra Amira",
                nisn = "0098765432",
                gender = "Perempuan",
                className = "2-B (Al-Khawarizmi)",
                birthPlace = "Samarinda",
                birthDate = "2017-05-20"
            )
        )
        updateState { it.copy(students = dummyStudents) }
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
            delay(1000)
            updateState {
                it.copy(
                    name = s.editedName,
                    phone = s.editedPhone,
                    isEditing = false,
                    isLoading = false
                )
            }
        }
    }

    fun refresh() {
        loadProfile()
        loadStudents()
    }
}
