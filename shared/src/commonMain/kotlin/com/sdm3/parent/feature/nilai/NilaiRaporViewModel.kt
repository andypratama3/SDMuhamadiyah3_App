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
    val semester: String = "ganjil",
    val availableSemesters: List<String> = emptyList()
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

    // Cache subjek per-semester dari transcript agar perpindahan semester instan
    // tanpa memanggil ulang jaringan.
    private var semesterSubjects: Map<String, List<GradeDto>> = emptyMap()

    // Urutan tampil semester yang masuk akal (periode dulu, lalu legacy).
    private val semesterOrder = listOf("ts1", "as1", "ts2", "at", "ganjil", "genap")

    private fun orderSemesters(keys: Collection<String>): List<String> =
        keys.distinct().sortedBy { key ->
            val idx = semesterOrder.indexOf(key.lowercase())
            if (idx == -1) Int.MAX_VALUE else idx
        }

    /**
     * Muat data awal: ambil transcript untuk tahu semester mana yang benar-benar
     * punya nilai, lalu pilih semester default yang tepat. Ini mencegah tampilan
     * kosong akibat default 'ganjil' padahal sekolah memakai periode ts1/as1/ts2/at.
     */
    fun loadInitial(studentId: String, requestedSemester: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null, studentId = studentId) }

            when (val result = gradeRepository.getTranscript(studentId)) {
                is ApiResult.Success -> {
                    val map = result.data.semesters.associate { it.semester to it.subjects }
                    semesterSubjects = map
                    val ordered = orderSemesters(map.keys)
                    val chosen = when {
                        requestedSemester in map -> requestedSemester
                        ordered.isNotEmpty() -> ordered.first()
                        else -> requestedSemester
                    }
                    val subjects = map[chosen].orEmpty()
                    updateState {
                        it.copy(
                            isLoading = false,
                            availableSemesters = ordered,
                            semester = chosen,
                            grades = subjects,
                            formatifGrades = emptyList(),
                            projekGrades = emptyList(),
                            isEmpty = subjects.isEmpty()
                        )
                    }
                }
                is ApiResult.Error -> {
                    // Transcript gagal (mis. jaringan): fallback ke endpoint grades biasa.
                    loadGrades(studentId, requestedSemester)
                }
            }
        }
    }

    /** Pilih semester dari dropdown — pakai cache transcript bila ada, kalau tidak fetch. */
    fun selectSemester(semester: String) {
        if (semester == uiState.value.semester) return
        val studentId = uiState.value.studentId
        val cached = semesterSubjects[semester]
        if (cached != null) {
            updateState {
                it.copy(
                    semester = semester,
                    grades = cached,
                    formatifGrades = emptyList(),
                    projekGrades = emptyList(),
                    isEmpty = cached.isEmpty(),
                    errorMessage = null,
                    isLoading = false
                )
            }
        } else {
            loadGrades(studentId, semester)
        }
    }

    fun loadGrades(studentId: String, semester: String? = null) {
        launchSafely {
            val sem = semester ?: uiState.value.semester
            updateState { it.copy(isLoading = true, errorMessage = null, studentId = studentId, semester = sem) }

            when (val result = gradeRepository.getGrades(studentId, sem)) {
                is ApiResult.Success -> {
                    val grades = result.data
                    // Data formatif & projek (P5) yang sebenarnya tersedia per-mapel
                    // di layar Detail Nilai (GradeComponentDto.componentType). Endpoint
                    // ringkasan ini hanya mengembalikan nilai sumatif per-mapel, jadi
                    // JANGAN mengarang data formatif/projek dari nilai sumatif.
                    updateState {
                        it.copy(
                            isLoading = false,
                            grades = grades,
                            formatifGrades = emptyList(),
                            projekGrades = emptyList(),
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
        loadInitial(s.studentId, s.semester)
    }
}
