package com.sdm3.parent.feature.nilai

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.security.SecureTokenManager
import com.sdm3.parent.data.remote.dto.GradeComponentDto
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
    private val gradeRepository: GradeRepositoryContract,
    private val secureTokenManager: SecureTokenManager,
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

    private fun persistSemester(semester: String) {
        secureTokenManager.saveLastNilaiSemester(semester)
    }

    private suspend fun loadComponentGrades(
        studentId: String,
        grades: List<GradeDto>,
    ): Pair<List<FormatifGradeItem>, List<ProjekGradeItem>> {
        val formatif = mutableListOf<FormatifGradeItem>()
        val projek = mutableListOf<ProjekGradeItem>()

        for (grade in grades) {
            when (val result = gradeRepository.getGradeComponents(studentId, grade.subjectId)) {
                is ApiResult.Success -> {
                    result.data.forEach { component ->
                        when (component.componentType.lowercase()) {
                            "formatif" -> formatif += component.toFormatifItem()
                            "projek" -> projek += component.toProjekItem()
                        }
                    }
                }
                is ApiResult.Error -> Unit
            }
        }
        return formatif to projek
    }

    private fun applyGradesState(
        semester: String,
        grades: List<GradeDto>,
        formatifGrades: List<FormatifGradeItem>,
        projekGrades: List<ProjekGradeItem>,
        availableSemesters: List<String> = uiState.value.availableSemesters,
    ) {
        persistSemester(semester)
        updateState {
            it.copy(
                isLoading = false,
                availableSemesters = availableSemesters,
                semester = semester,
                grades = grades,
                formatifGrades = formatifGrades,
                projekGrades = projekGrades,
                isEmpty = grades.isEmpty() && formatifGrades.isEmpty() && projekGrades.isEmpty(),
                selectedTab = 0,
            )
        }
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
                        requestedSemester.isNotBlank() && requestedSemester in map -> requestedSemester
                        ordered.isNotEmpty() -> ordered.last()
                        requestedSemester.isNotBlank() -> requestedSemester
                        else -> ordered.firstOrNull() ?: "ganjil"
                    }
                    val subjects = map[chosen].orEmpty()
                    val (formatif, projek) = loadComponentGrades(studentId, subjects)
                    applyGradesState(chosen, subjects, formatif, projek, ordered)
                }
                is ApiResult.Error -> {
                    // Transcript gagal (mis. jaringan): fallback ke endpoint grades biasa.
                    loadGrades(studentId, requestedSemester.takeIf { it.isNotBlank() })
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
            launchSafely {
                updateState { it.copy(isLoading = true, semester = semester) }
                val (formatif, projek) = loadComponentGrades(studentId, cached)
                applyGradesState(semester, cached, formatif, projek)
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
                    val (formatif, projek) = loadComponentGrades(studentId, grades)
                    applyGradesState(sem, grades, formatif, projek)
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

private fun GradeComponentDto.toFormatifItem() = FormatifGradeItem(
    code = subjectName,
    description = tpName ?: catatan ?: componentSubtype.orEmpty(),
    score = score?.toInt() ?: 0,
)

private fun GradeComponentDto.toProjekItem() = ProjekGradeItem(
    tema = subjectName,
    deskripsi = tpName ?: catatan.orEmpty(),
    nilai = score?.toInt() ?: 0,
    predikat = predicateFor(score),
)

private fun predicateFor(score: Double?): String = when {
    score == null -> "-"
    score >= 90 -> "A"
    score >= 80 -> "B"
    score >= 70 -> "C"
    else -> "D"
}
