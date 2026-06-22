package com.sdm3.parent.feature.infoanak

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.data.remote.dto.StudentDto
import kotlinx.coroutines.delay

data class DetailInfoAnakUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val student: StudentDto? = null
) : ScreenState

class DetailInfoAnakViewModel : BaseViewModel<DetailInfoAnakUiState>(DetailInfoAnakUiState()) {

    fun loadStudentDetail(id: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            delay(800) // Aesthetic delay
            val dummyStudent = StudentDto(
                id = id,
                name = "Aisyah Humaira",
                nisn = "0012345678",
                gender = "Perempuan",
                className = "4-A (Ibnu Sina)",
                birthPlace = "Jakarta",
                birthDate = "2014-05-12"
            )
            updateState {
                it.copy(
                    student = dummyStudent,
                    isLoading = false,
                    isEmpty = false
                )
            }
        }
    }

    fun refresh(studentId: String) {
        loadStudentDetail(studentId)
    }
}
