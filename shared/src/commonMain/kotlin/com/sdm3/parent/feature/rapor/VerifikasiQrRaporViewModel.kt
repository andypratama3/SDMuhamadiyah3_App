package com.sdm3.parent.feature.rapor

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.RaporVerifyResponse
import com.sdm3.parent.data.repository.RaporRepository

data class VerifikasiQrRaporUiState(
    val raporId: String = "",
    val qrInput: String = "",
    val verifyResult: RaporVerifyResponse? = null,
    val isVerified: Boolean = false,
    val showResult: Boolean = false,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false
) : ScreenState

class VerifikasiQrRaporViewModel(
    private val raporRepository: RaporRepository
) : BaseViewModel<VerifikasiQrRaporUiState>(VerifikasiQrRaporUiState()) {

    fun init(raporId: String) {
        updateState { it.copy(raporId = raporId) }
    }

    fun updateQrInput(input: String) {
        updateState { it.copy(qrInput = input) }
    }

    fun verify() {
        val input = uiState.value.qrInput
        if (input.isBlank()) {
            updateState { it.copy(errorMessage = "Masukkan data QR terlebih dahulu") }
            return
        }
        launchSafely(
            onError = { error ->
                updateState { it.copy(isLoading = false, errorMessage = error.message ?: "Gagal memverifikasi") }
            }
        ) {
            updateState { it.copy(isLoading = true, errorMessage = null, showResult = false) }
            when (val result = raporRepository.verifyQr(input)) {
                is ApiResult.Success -> {
                    val response = result.data
                    updateState {
                        it.copy(
                            isLoading = false,
                            isVerified = response.valid,
                            verifyResult = response,
                            showResult = true
                        )
                    }
                }
                is ApiResult.Error -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.error.toUserMessage(),
                            showResult = true,
                            isVerified = false
                        )
                    }
                }
            }
        }
    }

    fun reset() {
        updateState { VerifikasiQrRaporUiState(raporId = uiState.value.raporId) }
    }
}
