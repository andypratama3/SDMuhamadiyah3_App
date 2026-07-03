package com.sdm3.parent.feature.rapor

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.RaporInstanceDto
import com.sdm3.parent.domain.repository.RaporRepositoryContract

data class HalamanRaporUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val studentId: String = "",
    val rapors: List<RaporInstanceDto> = emptyList()
) : ScreenState

class HalamanRaporViewModel(
    private val raporRepository: RaporRepositoryContract
) : BaseViewModel<HalamanRaporUiState>(HalamanRaporUiState()) {

    fun loadRapors(studentId: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null, studentId = studentId) }
            when (val result = raporRepository.getRaporInstances(studentId)) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(
                            rapors = result.data,
                            isLoading = false,
                            isEmpty = result.data.isEmpty()
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

    fun refresh(studentId: String) {
        loadRapors(studentId)
    }
}
