package com.sdm3.parent.feature.nilai

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.data.remote.dto.GradeComponentDto
import kotlinx.coroutines.delay

data class DetailNilaiMapelUiState(
    val studentId: String = "",
    val subjectId: String = "",
    val subjectName: String = "",
    val components: List<GradeComponentDto> = emptyList(),
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = true
) : ScreenState

class DetailNilaiMapelViewModel : BaseViewModel<DetailNilaiMapelUiState>(DetailNilaiMapelUiState()) {

    fun loadComponents(studentId: String, subjectId: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null, studentId = studentId, subjectId = subjectId) }
            delay(800)
            
            val dummyComponents = listOf(
                GradeComponentDto(id = "1", subjectId = subjectId, subjectName = "Matematika", componentType = "sumatif", componentSubtype = "Sumatif 1", score = 95.0, tpName = "Bilangan Cacah", tpNumber = 1),
                GradeComponentDto(id = "2", subjectId = subjectId, subjectName = "Matematika", componentType = "sumatif", componentSubtype = "Sumatif 2", score = 90.0, tpName = "Operasi Hitung", tpNumber = 2),
                GradeComponentDto(id = "3", subjectId = subjectId, subjectName = "Matematika", componentType = "formatif", componentSubtype = "Formatif 1", score = 88.0, tpName = "Bangun Datar", tpNumber = 3)
            )

            updateState {
                it.copy(
                    isLoading = false,
                    components = dummyComponents,
                    subjectName = "Matematika",
                    isEmpty = false
                )
            }
        }
    }

    fun refresh() {
        val s = uiState.value
        if (s.studentId.isNotEmpty() && s.subjectId.isNotEmpty()) {
            loadComponents(s.studentId, s.subjectId)
        }
    }
}
