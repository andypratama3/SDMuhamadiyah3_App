package com.sdm3.parent.feature.auth

import com.sdm3.parent.core.base.MviViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.domain.repository.AuthRepositoryContract


data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoggedIn: Boolean = false,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
) : ScreenState

sealed interface LoginIntent {
    data class EmailChanged(val email: String) : LoginIntent
    data class PasswordChanged(val password: String) : LoginIntent
    data object Login : LoginIntent
    data object ClearError : LoginIntent
}

sealed interface LoginEffect {
    data object LoginSuccess : LoginEffect
}

class LoginViewModel(
    private val authRepository: AuthRepositoryContract
) : MviViewModel<LoginUiState, LoginIntent, LoginEffect>(LoginUiState()) {

    override fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> {
                updateState { it.copy(email = intent.email, errorMessage = null) }
            }
            is LoginIntent.PasswordChanged -> {
                updateState { it.copy(password = intent.password, errorMessage = null) }
            }
            is LoginIntent.Login -> login()
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
        launchSafely(
            onError = { error ->
                updateState { it.copy(isLoading = false, errorMessage = error.message ?: "Terjadi kesalahan") }
            }
        ) {
            updateState { it.copy(isLoading = true, errorMessage = null) }

            when (val result = authRepository.login(state.email, state.password)) {
                is ApiResult.Success -> {
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
}
