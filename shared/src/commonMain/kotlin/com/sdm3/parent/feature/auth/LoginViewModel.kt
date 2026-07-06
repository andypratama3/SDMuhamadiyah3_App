package com.sdm3.parent.feature.auth

import com.sdm3.parent.core.base.MviViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.security.BiometricAuthGate
import com.sdm3.parent.core.security.BiometricResult
import com.sdm3.parent.core.security.SecureTokenManager
import com.sdm3.parent.core.notification.FcmRegistrar
import com.sdm3.parent.core.network.sanitizeUserFacingMessage
import com.sdm3.parent.core.AppBranding
import com.sdm3.parent.domain.repository.AuthRepositoryContract


data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoggedIn: Boolean = false,
    val biometricAvailable: Boolean = false,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
) : ScreenState

private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

sealed interface LoginIntent {
    data class EmailChanged(val email: String) : LoginIntent
    data class PasswordChanged(val password: String) : LoginIntent
    data object Login : LoginIntent
    data object BiometricLogin : LoginIntent
    data object ClearError : LoginIntent
}

sealed interface LoginEffect {
    data object LoginSuccess : LoginEffect
}

class LoginViewModel(
    private val authRepository: AuthRepositoryContract,
    private val secureTokenManager: SecureTokenManager,
    private val biometricAuth: BiometricAuthGate,
    private val fcmRegistration: FcmRegistrar,
) : MviViewModel<LoginUiState, LoginIntent, LoginEffect>(LoginUiState()) {

    init {
        // Tampilkan opsi biometrik hanya bila pengguna sudah pernah login di
        // perangkat ini (token tersimpan) dan biometrik diaktifkan.
        val canBiometric = secureTokenManager.isBiometricEnabled() &&
            !secureTokenManager.getBearerToken().isNullOrBlank()
        updateState { it.copy(biometricAvailable = canBiometric) }
    }

    override fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> {
                updateState { it.copy(email = intent.email, errorMessage = null) }
            }
            is LoginIntent.PasswordChanged -> {
                updateState { it.copy(password = intent.password, errorMessage = null) }
            }
            is LoginIntent.Login -> login()
            is LoginIntent.BiometricLogin -> biometricLogin()
            is LoginIntent.ClearError -> {
                updateState { it.copy(errorMessage = null) }
            }
        }
    }

    private fun login() {
        val state = uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            updateState { it.copy(errorMessage = "Email dan password harus diisi") }
            return
        }
        if (!EMAIL_REGEX.matches(state.email.trim())) {
            updateState { it.copy(errorMessage = "Format email tidak valid") }
            return
        }
        launchSafely(
            onError = { error ->
                updateState { it.copy(isLoading = false, errorMessage = sanitizeUserFacingMessage(error.message)) }
            }
        ) {
            updateState { it.copy(isLoading = true, errorMessage = null) }

            when (val result = authRepository.login(state.email.trim(), state.password)) {
                is ApiResult.Success -> {
                    if (secureTokenManager.getRoleContext().hasParentAccess) {
                        fcmRegistration.registerIfAvailable()
                    }
                    updateState { it.copy(isLoading = false, isLoggedIn = true) }
                    sendEffect(LoginEffect.LoginSuccess)
                }
                is ApiResult.Error -> {
                    val msg = result.error.toUserMessage()
                    updateState { it.copy(isLoading = false, errorMessage = msg) }
                }
            }
        }
    }

    private fun biometricLogin() {
        val token = secureTokenManager.getBearerToken()
        if (token.isNullOrBlank()) {
            updateState { it.copy(errorMessage = "Silakan masuk dengan email terlebih dahulu untuk mengaktifkan biometrik") }
            return
        }
        launchSafely(
            onError = { error ->
                updateState { it.copy(isLoading = false, errorMessage = sanitizeUserFacingMessage(error.message)) }
            }
        ) {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            when (val result = biometricAuth.authenticate(AppBranding.BIOMETRIC_LOGIN_PROMPT)) {
                BiometricResult.Success -> {
                    when (val userResult = authRepository.getAuthenticatedUser()) {
                        is ApiResult.Success -> {
                            if (secureTokenManager.getRoleContext().hasParentAccess) {
                                fcmRegistration.registerIfAvailable()
                            }
                            updateState { it.copy(isLoading = false) }
                            sendEffect(LoginEffect.LoginSuccess)
                        }
                        is ApiResult.Error -> {
                            secureTokenManager.clearAllSecureData()
                            updateState {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = userResult.error.toUserMessage()
                                )
                            }
                        }
                    }
                }
                BiometricResult.NotAvailable -> {
                    updateState { it.copy(isLoading = false, errorMessage = "Biometrik tidak tersedia di perangkat ini") }
                }
                is BiometricResult.Error -> {
                    updateState { it.copy(isLoading = false, errorMessage = sanitizeUserFacingMessage(result.message)) }
                }
            }
        }
    }
}
