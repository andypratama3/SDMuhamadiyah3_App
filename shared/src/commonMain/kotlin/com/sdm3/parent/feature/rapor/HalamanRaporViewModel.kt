package com.sdm3.parent.feature.rapor

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.RaporInstanceDto
import com.sdm3.parent.data.repository.RaporRepository

data class HalamanRaporUiState(
    val studentId: String = "",
    val raporInstances: List<RaporInstanceDto> = emptyList(),
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = true,
    val downloadUrl: String? = null
) : ScreenState

class HalamanRaporViewModel(
    private val raporRepository: RaporRepository
) : BaseViewModel<HalamanRaporUiState>(HalamanRaporUiState()) {

    fun loadRaporInstances(studentId: String) {
        launchSafely(
            onError = { error ->
                updateState { it.copy(isLoading = false, errorMessage = error.message ?: "Terjadi kesalahan") }
            }
        ) {
            updateState { it.copy(isLoading = true, errorMessage = null, studentId = studentId) }
            when (val result = raporRepository.getRaporInstances(studentId)) {
                is ApiResult.Success -> {
                    val instances = result.data
                    updateState { it.copy(isLoading = false, raporInstances = instances, isEmpty = instances.isEmpty()) }
                }
                is ApiResult.Error -> {
                    updateState { it.copy(isLoading = false, errorMessage = result.error.toUserMessage()) }
                }
            }
        }
    }

    fun downloadRapor(id: String) {
        launchSafely(
            onError = { error ->
                updateState { it.copy(isLoading = false, errorMessage = error.message ?: "Terjadi kesalahan") }
            }
        ) {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            when (val result = raporRepository.getDownloadUrl(id)) {
                is ApiResult.Success<String> -> {
                    updateState { it.copy(isLoading = false, downloadUrl = result.data) }
                }
                is ApiResult.Error -> {
                    updateState { it.copy(isLoading = false, errorMessage = result.error.toUserMessage()) }
                }
            }
        }
    }

    fun refresh() {
        val s = uiState.value
        if (s.studentId.isNotEmpty()) {
            loadRaporInstances(s.studentId)
        }
    }
}
