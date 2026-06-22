package com.sdm3.parent.feature.rapor

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.data.remote.dto.RaporVerifyResponse
import kotlinx.coroutines.delay

data class VerifikasiQrRaporUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val qrInput: String = "",
    val showResult: Boolean = false,
    val verifyResult: RaporVerifyResponse? = null
) : ScreenState

class VerifikasiQrRaporViewModel : BaseViewModel<VerifikasiQrRaporUiState>(VerifikasiQrRaporUiState()) {

    fun init(raporId: String) {
        // Option to pre-fill if navigating from a specific rapor
        updateState { it.copy(qrInput = raporId) }
    }

    fun updateQrInput(input: String) {
        updateState { it.copy(qrInput = input, errorMessage = null) }
    }

    fun verify() {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null, showResult = false) }
            delay(1500)
            
            // Dummy logic: if input contains "error", show error, else valid
            if (uiState.value.qrInput.contains("error", ignoreCase = true)) {
                updateState { it.copy(isLoading = false, errorMessage = "Kode verifikasi tidak valid atau tidak ditemukan.") }
            } else {
                updateState {
                    it.copy(
                        isLoading = false,
                        showResult = true,
                        verifyResult = RaporVerifyResponse(
                            valid = true,
                            message = "Dokumen ini sah secara digital melalui sistem SDM3 Parent Portal.",
                            studentName = "Ahmad Zaki Al-Fatih",
                            nisn = "0012345678"
                        )
                    )
                }
            }
        }
    }

    fun reset() {
        updateState { VerifikasiQrRaporUiState() }
    }
}
