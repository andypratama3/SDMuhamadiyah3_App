package com.sdm3.parent.feature.auth

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class OtpStep {
    REQUEST_OTP, VERIFY_OTP, RESET_PASSWORD
}

data class VerifikasiOtpUiState(
    val email: String = "",
    val otpCode: String = "",
    val newPassword: String = "",
    val newPasswordConfirmation: String = "",
    val isPasswordVisible: Boolean = false,
    val countdownSeconds: Int = 60,
    val canResend: Boolean = false,
    val step: OtpStep = OtpStep.REQUEST_OTP,
    val resetSuccessMessage: String? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false
) : ScreenState

class VerifikasiOtpViewModel : BaseViewModel<VerifikasiOtpUiState>(VerifikasiOtpUiState()) {

    private var countdownJob: Job? = null

    fun setEmail(email: String) {
        updateState { it.copy(email = email, errorMessage = null) }
    }

    fun updateOtpCode(code: String) {
        if (code.length <= 6 && code.all { it.isDigit() }) {
            updateState { it.copy(otpCode = code, errorMessage = null) }
        }
    }

    fun updateNewPassword(password: String) {
        updateState { it.copy(newPassword = password, errorMessage = null) }
    }

    fun updateNewPasswordConfirmation(password: String) {
        updateState { it.copy(newPasswordConfirmation = password, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        updateState { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun requestOtp() {
        val state = uiState.value
        if (state.email.isBlank()) {
            updateState { it.copy(errorMessage = "Email harus diisi") }
            return
        }
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            delay(1000) // Dummy delay
            updateState {
                it.copy(
                    isLoading = false,
                    step = OtpStep.VERIFY_OTP,
                    countdownSeconds = 60,
                    canResend = false
                )
            }
            startCountdown()
        }
    }

    fun verifyOtp() {
        val state = uiState.value
        if (state.otpCode.length < 6) {
            updateState { it.copy(errorMessage = "Masukkan 6 digit kode OTP") }
            return
        }
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            delay(1000) // Dummy delay
            updateState {
                it.copy(isLoading = false, step = OtpStep.RESET_PASSWORD)
            }
        }
    }

    fun resetPassword() {
        val state = uiState.value
        if (state.newPassword.length < 6) {
            updateState { it.copy(errorMessage = "Password minimal 6 karakter") }
            return
        }
        if (state.newPassword != state.newPasswordConfirmation) {
            updateState { it.copy(errorMessage = "Password tidak cocok") }
            return
        }
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            delay(1000) // Dummy delay
            updateState {
                it.copy(isLoading = false, resetSuccessMessage = "Kata sandi berhasil diperbarui!")
            }
        }
    }

    fun resendOtp() {
        countdownJob?.cancel()
        updateState { it.copy(countdownSeconds = 60, canResend = false) }
        requestOtp()
    }

    fun clearError() {
        updateState { it.copy(errorMessage = null) }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (uiState.value.countdownSeconds > 0) {
                delay(1000L)
                updateState { it.copy(countdownSeconds = it.countdownSeconds - 1) }
            }
            updateState { it.copy(canResend = true) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
