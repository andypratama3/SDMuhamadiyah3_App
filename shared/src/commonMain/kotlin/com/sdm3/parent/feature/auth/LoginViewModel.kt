package com.sdm3.parent.feature.auth

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.UserDto
import com.sdm3.parent.data.repository.AuthRepository

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoggedIn: Boolean = false,
    val user: UserDto? = null,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false
) : ScreenState

class LoginViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<LoginUiState>(LoginUiState()) {

    fun onEmailChanged(email: String) {
        updateState { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        updateState { it.copy(password = password, errorMessage = null) }
    }

    fun login() {
        val state = uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            updateState { it.copy(errorMessage = "Email dan password harus diisi") }
            return
        }
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.login(state.email, state.password)) {
                is ApiResult.Success -> {
                    updateState { it.copy(isLoading = false, isLoggedIn = true, user = result.data) }
                }
                is ApiResult.Error -> {
                    updateState { it.copy(isLoading = false, errorMessage = result.error.toUserMessage()) }
                }
            }
        }
    }

    fun clearError() {
        updateState { it.copy(errorMessage = null) }
    }
}
