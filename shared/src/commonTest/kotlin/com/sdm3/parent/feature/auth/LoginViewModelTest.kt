package com.sdm3.parent.feature.auth

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.test.TestDispatcher
import com.sdm3.parent.data.remote.dto.UserDto
import com.sdm3.parent.data.repository.AuthRepositoryContract
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FakeAuthRepository : AuthRepositoryContract {
    var loginResult: ApiResult<UserDto> = ApiResult.Success(
        UserDto(id = "1", name = "Test User", email = "test@example.com")
    )

    override suspend fun login(email: String, password: String): ApiResult<UserDto> = loginResult

    override suspend fun getAuthenticatedUser(): ApiResult<UserDto> = loginResult

    override suspend fun isLoggedIn(): Boolean = loginResult is ApiResult.Success

    override suspend fun logout() {}

    override suspend fun requestOtp(email: String): ApiResult<String> =
        ApiResult.Success("OTP sent")

    override suspend fun verifyOtp(email: String, otp: String): ApiResult<String> =
        ApiResult.Success("OTP verified")

    override suspend fun resetPassword(
        email: String, otp: String, password: String, passwordConfirmation: String
    ): ApiResult<String> = ApiResult.Success("Password reset")
}

class LoginViewModelTest : TestDispatcher() {

    @Test
    fun initialUiStateHasDefaultValues() {
        val repo = FakeAuthRepository()
        val viewModel = LoginViewModel(repo)

        val state = viewModel.uiState.value
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertEquals(false, state.isLoggedIn)
        assertNull(state.user)
        assertEquals(false, state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun onEmailChangedUpdatesEmail() {
        val repo = FakeAuthRepository()
        val viewModel = LoginViewModel(repo)

        viewModel.onEmailChanged("test@example.com")

        assertEquals("test@example.com", viewModel.uiState.value.email)
    }

    @Test
    fun onEmailChangedClearsErrorMessage() {
        val repo = FakeAuthRepository()
        val viewModel = LoginViewModel(repo)

        viewModel.onEmailChanged("test@example.com")

        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun onPasswordChangedUpdatesPassword() {
        val repo = FakeAuthRepository()
        val viewModel = LoginViewModel(repo)

        viewModel.onPasswordChanged("secret")

        assertEquals("secret", viewModel.uiState.value.password)
    }

    @Test
    fun onPasswordChangedClearsErrorMessage() {
        val repo = FakeAuthRepository()
        val viewModel = LoginViewModel(repo)

        viewModel.onPasswordChanged("secret")

        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun loginWithBlankEmailShowsError() {
        val repo = FakeAuthRepository()
        val viewModel = LoginViewModel(repo)

        viewModel.login()

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals(false, viewModel.uiState.value.isLoggedIn)
    }

    @Test
    fun loginWithBlankPasswordShowsError() {
        val repo = FakeAuthRepository()
        val viewModel = LoginViewModel(repo)

        viewModel.onEmailChanged("test@example.com")
        viewModel.login()

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals(false, viewModel.uiState.value.isLoggedIn)
    }

    @Test
    fun clearErrorResetsErrorMessage() {
        val repo = FakeAuthRepository()
        val viewModel = LoginViewModel(repo)

        viewModel.clearError()

        assertNull(viewModel.uiState.value.errorMessage)
    }
}
