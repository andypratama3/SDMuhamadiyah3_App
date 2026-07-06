package com.sdm3.parent.feature.infoanak

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.AcademicProgramDto
import com.sdm3.parent.data.remote.dto.ExtracurricularDto
import com.sdm3.parent.core.network.sanitizeUserFacingMessage
import com.sdm3.parent.domain.repository.ExtracurricularRepositoryContract

data class KegiatanProgramUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val extracurriculars: List<ExtracurricularDto> = emptyList(),
    val academicPrograms: List<AcademicProgramDto> = emptyList()
) : ScreenState

class KegiatanProgramViewModel(
    private val extracurricularRepository: ExtracurricularRepositoryContract
) : BaseViewModel<KegiatanProgramUiState>(KegiatanProgramUiState()) {

    fun loadActivities(studentId: String) {
        launchSafely(
            onError = { error ->
                updateState { it.copy(isLoading = false, errorMessage = sanitizeUserFacingMessage(error.message)) }
            }
        ) {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            val ekskulResult = extracurricularRepository.getExtracurriculars(studentId)
            val programResult = extracurricularRepository.getAcademicPrograms(studentId)

            // Bila KEDUA endpoint gagal, tampilkan error (bukan state kosong)
            // agar pesan sesi kedaluwarsa/koneksi tetap sampai ke pengguna.
            if (ekskulResult is ApiResult.Error && programResult is ApiResult.Error) {
                updateState {
                    it.copy(isLoading = false, errorMessage = ekskulResult.error.toUserMessage())
                }
                return@launchSafely
            }

            val ekskul = if (ekskulResult is ApiResult.Success) ekskulResult.data else emptyList()
            val programs = if (programResult is ApiResult.Success) programResult.data else emptyList()

            updateState {
                it.copy(
                    extracurriculars = ekskul,
                    academicPrograms = programs,
                    isLoading = false,
                    isEmpty = ekskul.isEmpty() && programs.isEmpty()
                )
            }
        }
    }

    fun refresh(studentId: String) {
        loadActivities(studentId)
    }
}
