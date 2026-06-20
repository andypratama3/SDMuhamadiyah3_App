package com.sdm3.parent.feature.kegiatan

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.ExtracurricularDto
import com.sdm3.parent.data.repository.ExtracurricularRepository

data class KegiatanProgramUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val extracurriculars: List<ExtracurricularDto> = emptyList()
) : ScreenState

class KegiatanProgramViewModel(
    private val extracurricularRepository: ExtracurricularRepository
) : BaseViewModel<KegiatanProgramUiState>(KegiatanProgramUiState()) {

    fun loadActivities(studentId: String) {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        launchSafely {
            when (val result = extracurricularRepository.getExtracurriculars(studentId)) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(
                            extracurriculars = result.data,
                            isLoading = false,
                            isEmpty = result.data.isEmpty()
                        )
                    }
                }
                is ApiResult.Error -> {
                    updateState { it.copy(isLoading = false, errorMessage = result.error.toUserMessage()) }
                }
            }
        }
    }

    fun refresh(studentId: String) {
        loadActivities(studentId)
    }
}
