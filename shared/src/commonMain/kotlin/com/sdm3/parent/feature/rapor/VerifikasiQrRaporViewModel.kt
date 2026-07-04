package com.sdm3.parent.feature.rapor

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.RaporVerifyResponse
import com.sdm3.parent.domain.repository.RaporRepositoryContract

data class VerifikasiQrRaporUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val qrInput: String = "",
    val showResult: Boolean = false,
    val verifyResult: RaporVerifyResponse? = null
) : ScreenState

class VerifikasiQrRaporViewModel(
    private val raporRepository: RaporRepositoryContract
) : BaseViewModel<VerifikasiQrRaporUiState>(VerifikasiQrRaporUiState()) {

    fun init(raporId: String) {
        updateState { it.copy(qrInput = raporId) }
    }

    fun updateQrInput(input: String) {
        updateState { it.copy(qrInput = input, errorMessage = null) }
    }

    fun verify() {
        val qrInput = uiState.value.qrInput
        if (qrInput.isBlank()) {
            updateState { it.copy(errorMessage = "Masukkan kode QR atau ID rapor") }
            return
        }
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null, showResult = false) }
            when (val result = raporRepository.verifyQr(qrInput)) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            showResult = true,
                            verifyResult = result.data
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

    fun reset() {
        // Pertahankan input QR/ID yang sudah ada agar pengguna bisa memverifikasi
        // ulang tanpa kehilangan prefill dari daftar rapor.
        updateState {
            VerifikasiQrRaporUiState(qrInput = it.qrInput)
        }
    }
}
