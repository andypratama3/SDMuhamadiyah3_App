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
        updateState {
            it.copy(
                raporId = raporId,
                downloadUrl = downloadUrl,
                fileName = fileNameFromUrl(downloadUrl)
            )
        }
    }

    fun download() {
        launchSafely(
            onError = { error ->
                // Bila resolusi URL gagal tapi kita sudah punya URL dari daftar rapor,
                // tetap izinkan dokumen dibuka memakai URL tersebut.
                val existing = uiState.value.downloadUrl
                if (existing.isNotBlank()) {
                    updateState { it.copy(isDownloading = false, isDownloaded = true, downloadProgress = 1f, errorMessage = null) }
                } else {
                    updateState { it.copy(isDownloading = false, errorMessage = error.message ?: "Gagal mengunduh") }
                }
            }
        ) {
            updateState { it.copy(isDownloading = true, downloadProgress = 0f, errorMessage = null) }
            when (val result = raporRepository.getDownloadUrl(uiState.value.raporId)) {
                is ApiResult.Success -> {
                    val url = result.data.ifBlank { uiState.value.downloadUrl }
                    updateState {
                        it.copy(
                            isDownloading = false,
                            isDownloaded = url.isNotBlank(),
                            downloadProgress = 1f,
                            downloadUrl = url,
                            fileName = fileNameFromUrl(url),
                            errorMessage = if (url.isBlank()) "Tautan dokumen tidak tersedia" else null
                        )
                    }
                }
                is ApiResult.Error -> {
                    // Fallback ke URL yang sudah dikirim dari layar daftar rapor.
                    val existing = uiState.value.downloadUrl
                    if (existing.isNotBlank()) {
                        updateState { it.copy(isDownloading = false, isDownloaded = true, downloadProgress = 1f, errorMessage = null) }
                    } else {
                        updateState { it.copy(isDownloading = false, errorMessage = result.error.toUserMessage()) }
                    }
                }
            }
        }
    }

    fun retry() {
        updateState { it.copy(errorMessage = null) }
        download()
    }

    fun resetDownload() {
        updateState { it.copy(isDownloading = false, isDownloaded = false, downloadProgress = 0f, errorMessage = null) }
    }

    private fun fileNameFromUrl(url: String): String {
        val name = url.substringBefore('?').substringAfterLast('/')
        return if (name.isNotBlank() && name.contains('.')) name else ""
    }
}
