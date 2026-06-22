package com.sdm3.parent.feature.nilai

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.data.remote.dto.GradeDto
import kotlinx.coroutines.delay

data class NilaiRaporUiState(
    val studentId: String = "",
    val grades: List<GradeDto> = emptyList(),
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = true,
    val selectedTab: Int = 0,
    val semester: String = "ganjil"
) : ScreenState

class NilaiRaporViewModel : BaseViewModel<NilaiRaporUiState>(NilaiRaporUiState()) {

    fun loadGrades(studentId: String, semester: String? = null) {
        launchSafely {
            val sem = semester ?: uiState.value.semester
            updateState { it.copy(isLoading = true, errorMessage = null, studentId = studentId, semester = sem) }
            delay(1000)
            
            val dummyGrades = listOf(
                GradeDto(id = "1", subjectId = "m1", subjectName = "Matematika", score = 92.0, predicate = "A", semester = sem),
                GradeDto(id = "2", subjectId = "m2", subjectName = "Bahasa Indonesia", score = 88.0, predicate = "B+", semester = sem),
                GradeDto(id = "3", subjectId = "m3", subjectName = "IPA", score = 95.0, predicate = "A", semester = sem),
                GradeDto(id = "4", subjectId = "m4", subjectName = "IPS", score = 78.0, predicate = "B", semester = sem),
                GradeDto(id = "5", subjectId = "m5", subjectName = "Pend. Agama", score = 90.0, predicate = "A", semester = sem),
                GradeDto(id = "6", subjectId = "m6", subjectName = "PJOK", score = 85.0, predicate = "B+", semester = sem),
                GradeDto(id = "7", subjectId = "m7", subjectName = "Seni Budaya", score = 82.0, predicate = "B", semester = sem),
                GradeDto(id = "8", subjectId = "m8", subjectName = "Bahasa Inggris", score = 76.0, predicate = "B", semester = sem)
            )

            updateState { it.copy(isLoading = false, grades = dummyGrades, isEmpty = false) }
        }
    }

    fun selectTab(index: Int) {
        updateState { it.copy(selectedTab = index) }
    }

    fun refresh() {
        val s = uiState.value
        loadGrades(s.studentId, s.semester)
    }
}
