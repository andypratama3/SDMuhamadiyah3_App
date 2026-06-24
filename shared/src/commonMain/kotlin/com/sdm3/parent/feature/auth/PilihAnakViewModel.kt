package com.sdm3.parent.feature.auth

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.data.remote.dto.StudentDto
import com.sdm3.parent.data.repository.StudentRepository
import kotlinx.coroutines.delay

data class PilihAnakUiState(
    val students: List<StudentDto> = emptyList(),
    val selectedStudentId: String? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = true
) : ScreenState

class PilihAnakViewModel(
    private val studentRepository: StudentRepository
) : BaseViewModel<PilihAnakUiState>(PilihAnakUiState()) {

    init {
        loadStudents()
    }

    fun loadStudents() {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            // Simulasi dummy data untuk sementara
            delay(1000)
            val dummyStudents = listOf(
                StudentDto(
                    id = "1",
                    name = "Ahmad Fatih",
                    nisn = "0123456789",
                    nis = "12345",
                    gender = "Laki-laki",
                    birthPlace = "Samarinda",
                    birthDate = "2015-05-10",
                    className = "4-A",
                    photo = null
                ),
                StudentDto(
                    id = "2",
                    name = "Siti Aminah",
                    nisn = "0987654321",
                    nis = "54321",
                    gender = "Perempuan",
                    birthPlace = "Samarinda",
                    birthDate = "2017-08-15",
                    className = "2-B",
                    photo = null
                )
            )
            updateState {
                it.copy(
                    isLoading = false,
                    students = dummyStudents,
                    isEmpty = dummyStudents.isEmpty()
                )
            }
        }
    }

    fun selectStudent(studentId: String) {
        updateState { it.copy(selectedStudentId = studentId) }
    }
}
