package com.sdm3.parent.feature.auth

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.data.remote.dto.UserDto
import com.sdm3.parent.domain.repository.AuthRepositoryContract
import kotlinx.coroutines.delay

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
    private val authRepository: AuthRepositoryContract
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
            
            // Simulasi dummy login untuk sementara
            delay(1500)
            
            if (state.password == "error") {
                updateState { it.copy(isLoading = false, errorMessage = "Email atau password salah") }
            } else {
                val dummyUser = UserDto(
                    id = "parent-123",
                    name = "Orang Tua Dummy",
                    email = state.email,
                    phone = "08123456789",
                    avatar = null
                )
                updateState { it.copy(isLoading = false, isLoggedIn = true, user = dummyUser) }
            }
        }
    }

    fun clearError() {
        updateState { it.copy(errorMessage = null) }
    }
}
