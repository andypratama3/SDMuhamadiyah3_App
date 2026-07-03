package com.sdm3.parent.feature.nilai

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.GradeDto
import com.sdm3.parent.domain.repository.GradeRepositoryContract

data class NilaiRaporUiState(
    val studentId: String = "",
    val grades: List<GradeDto> = emptyList(),
    val formatifGrades: List<FormatifGradeItem> = emptyList(),
    val projekGrades: List<ProjekGradeItem> = emptyList(),
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = true,
    val selectedTab: Int = 0,
    val semester: String = "ganjil"
) : ScreenState

data class FormatifGradeItem(
    val code: String,
    val description: String,
    val score: Int
)

data class ProjekGradeItem(
    val tema: String,
    val deskripsi: String,
    val nilai: Int,
    val predikat: String
)

class NilaiRaporViewModel(
    private val gradeRepository: GradeRepositoryContract
) : BaseViewModel<NilaiRaporUiState>(NilaiRaporUiState()) {

    fun loadGrades(studentId: String, semester: String? = null) {
        launchSafely {
            val sem = semester ?: uiState.value.semester
            updateState { it.copy(isLoading = true, errorMessage = null, studentId = studentId, semester = sem) }

            when (val result = gradeRepository.getGrades(studentId, sem)) {
                is ApiResult.Success -> {
                    val grades = result.data
                    val formatif = grades.mapIndexedNotNull { index, g ->
                        g.score?.let {
                            FormatifGradeItem(
                                code = "TP ${index + 1}.1",
                                description = g.subjectName,
                                score = it.toInt()
                            )
                        }
                    }
                    val projek = grades.mapIndexedNotNull { index, g ->
                        g.score?.let {
                            ProjekGradeItem(
                                tema = "Projek ${index + 1}",
                                deskripsi = g.subjectName,
                                nilai = it.toInt(),
                                predikat = g.predicate ?: "B"
                            )
                        }
                    }
                    updateState {
                        it.copy(
                            isLoading = false,
                            grades = grades,
                            formatifGrades = formatif,
                            projekGrades = projek,
                            isEmpty = grades.isEmpty()
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

    fun selectTab(index: Int) {
        updateState { it.copy(selectedTab = index) }
    }

    fun refresh() {
        val s = uiState.value
        loadGrades(s.studentId, s.semester)
    }
}
