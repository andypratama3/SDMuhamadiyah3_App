package com.sdm3.parent.feature.auth

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.notification.FcmRegistrar
import com.sdm3.parent.core.security.SecureTokenManager
import com.sdm3.parent.domain.repository.AuthRepositoryContract

data class AccountDeletionUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    val reasonText: String = "",
    val isConfirmDialogShown: Boolean = false,
    val isRequestSubmitted: Boolean = false,
    override val isEmpty: Boolean = false
) : ScreenState

class AccountDeletionViewModel(
    private val authRepository: AuthRepositoryContract,
    private val secureTokenManager: SecureTokenManager,
    private val fcmRegistration: FcmRegistrar
) : BaseViewModel<AccountDeletionUiState>(AccountDeletionUiState()) {

    fun updateReason(reason: String) {
        updateState { it.copy(reasonText = reason, errorMessage = null) }
    }

    fun showConfirmDialog() {
        if (uiState.value.reasonText.isBlank()) {
            updateState { it.copy(errorMessage = "Mohon tuliskan alasan penghapusan akun") }
            return
        }
        updateState { it.copy(isConfirmDialogShown = true, errorMessage = null) }
    }

    fun dismissConfirmDialog() {
        updateState { it.copy(isConfirmDialogShown = false) }
    }

    fun submitDeletionRequest() {
        val reason = uiState.value.reasonText
        launchSafely(
            onError = { error ->
                updateState {
                    it.copy(
                        isLoading = false,
                        isConfirmDialogShown = false,
                        errorMessage = error.message ?: "Gagal mengajukan penghapusan akun"
                    )
                }
            }
        ) {
            updateState { it.copy(isLoading = true, isConfirmDialogShown = false, errorMessage = null) }
            when (val result = authRepository.deleteAccount(reason)) {
                is ApiResult.Success -> {
                    fcmRegistration.unregisterIfNeeded()
                    authRepository.logout()
                    updateState { it.copy(isLoading = false, isRequestSubmitted = true) }
                }
                is ApiResult.Error -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.error.toUserMessage()
                        )
                    }
                }
            }
        }
    }
}
