package com.sdm3.parent.feature.kegiatan

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.data.remote.dto.ExtracurricularDto
import kotlinx.coroutines.delay

data class KegiatanProgramUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val extracurriculars: List<ExtracurricularDto> = emptyList()
) : ScreenState

class KegiatanProgramViewModel : BaseViewModel<KegiatanProgramUiState>(KegiatanProgramUiState()) {

    fun loadActivities(studentId: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            delay(1000)
            val dummyEkskul = listOf(
                ExtracurricularDto(id = "1", name = "Sepak Bola", teacherName = "Coach Hendra", description = "Ananda memiliki koordinasi tim yang sangat baik dan disiplin dalam berlatih fisik."),
                ExtracurricularDto(id = "2", name = "Seni Lukis", teacherName = "Ibu Maya", description = "Kreativitas dalam pemilihan warna sudah menonjol, perlu ditingkatkan pada detail anatomi."),
                ExtracurricularDto(id = "3", name = "Robotik", teacherName = "Bp. Anwar", description = "Sangat cepat memahami logika pemrograman dasar dan antusias merakit sensor.")
            )
            updateState {
                it.copy(
                    extracurriculars = dummyEkskul,
                    isLoading = false,
                    isEmpty = dummyEkskul.isEmpty()
                )
            }
        }
    }

    fun refresh(studentId: String) {
        loadActivities(studentId)
    }
}
