package com.sdm3.parent.feature.auth

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.test.TestDispatcher
import com.sdm3.parent.data.remote.dto.UserDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeAuthRepository {
    var loginResult: ApiResult<UserDto> = ApiResult.Success(
        UserDto(id = "1", name = "Test User", email = "test@example.com")
    )

    suspend fun login(email: String, password: String): ApiResult<UserDto> = loginResult
}

class LoginViewModelTest : TestDispatcher() {

    @Test
    fun initialUiStateHasDefaultValues() {
        val repo = FakeAuthRepository()
        val viewModel = LoginViewModel(repo as com.sdm3.parent.data.repository.AuthRepository)

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
        val viewModel = LoginViewModel(repo as com.sdm3.parent.data.repository.AuthRepository)

        viewModel.onEmailChanged("test@example.com")

        assertEquals("test@example.com", viewModel.uiState.value.email)
    }

    @Test
    fun onEmailChangedClearsErrorMessage() {
        val repo = FakeAuthRepository()
        val viewModel = LoginViewModel(repo as com.sdm3.parent.data.repository.AuthRepository)

        viewModel.onEmailChanged("test@example.com")

        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun onPasswordChangedUpdatesPassword() {
        val repo = FakeAuthRepository()
        val viewModel = LoginViewModel(repo as com.sdm3.parent.data.repository.AuthRepository)

        viewModel.onPasswordChanged("secret")

        assertEquals("secret", viewModel.uiState.value.password)
    }

    @Test
    fun onPasswordChangedClearsErrorMessage() {
        val repo = FakeAuthRepository()
        val viewModel = LoginViewModel(repo as com.sdm3.parent.data.repository.AuthRepository)

        viewModel.onPasswordChanged("secret")

        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun loginWithBlankEmailShowsError() {
        val repo = FakeAuthRepository()
        val viewModel = LoginViewModel(repo as com.sdm3.parent.data.repository.AuthRepository)

        viewModel.login()

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals(false, viewModel.uiState.value.isLoggedIn)
    }

    @Test
    fun loginWithBlankPasswordShowsError() {
        val repo = FakeAuthRepository()
        val viewModel = LoginViewModel(repo as com.sdm3.parent.data.repository.AuthRepository)

        viewModel.onEmailChanged("test@example.com")
        viewModel.login()

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals(false, viewModel.uiState.value.isLoggedIn)
    }

    @Test
    fun clearErrorResetsErrorMessage() {
        val repo = FakeAuthRepository()
        val viewModel = LoginViewModel(repo as com.sdm3.parent.data.repository.AuthRepository)

        viewModel.clearError()

        assertNull(viewModel.uiState.value.errorMessage)
    }
}
