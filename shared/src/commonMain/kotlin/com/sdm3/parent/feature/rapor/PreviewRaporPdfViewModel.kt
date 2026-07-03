package com.sdm3.parent.feature.rapor

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.domain.repository.RaporRepositoryContract

data class PreviewRaporPdfUiState(
    val raporId: String = "",
    val downloadUrl: String = "",
    val fileName: String = "",
    val fileSize: String = "",
    val downloadProgress: Float = 0f,
    val isDownloading: Boolean = false,
    val isDownloaded: Boolean = false,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false
) : ScreenState

class PreviewRaporPdfViewModel(
    private val raporRepository: RaporRepositoryContract
) : BaseViewModel<PreviewRaporPdfUiState>(PreviewRaporPdfUiState()) {

    fun init(raporId: String, downloadUrl: String) {
        updateState { it.copy(raporId = raporId, downloadUrl = downloadUrl) }
    }

    fun download() {
        launchSafely(
            onError = { error ->
                updateState { it.copy(isDownloading = false, errorMessage = error.message ?: "Gagal mengunduh") }
            }
        ) {
            updateState { it.copy(isDownloading = true, downloadProgress = 0f, errorMessage = null) }
            when (val result = raporRepository.getDownloadUrl(uiState.value.raporId)) {
                is ApiResult.Success -> {
                    updateState { it.copy(isDownloading = false, isDownloaded = true, downloadProgress = 1f, downloadUrl = result.data) }
                }
                is ApiResult.Error -> {
                    updateState { it.copy(isDownloading = false, errorMessage = result.error.toUserMessage()) }
                }
            }
        }
    }

    fun resetDownload() {
        updateState { it.copy(isDownloading = false, isDownloaded = false, downloadProgress = 0f, errorMessage = null) }
    }
}
