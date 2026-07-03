package com.sdm3.parent.feature.auth

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.notification.FcmRegistrar
import com.sdm3.parent.core.security.BiometricAuthGate
import com.sdm3.parent.core.security.BiometricResult
import com.sdm3.parent.core.security.SecureTokenManager
import com.sdm3.parent.core.security.SecureStorage
import com.sdm3.parent.core.test.TestDispatcher
import com.sdm3.parent.data.remote.dto.UserDto
import com.sdm3.parent.domain.repository.AuthRepositoryContract
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

    override suspend fun deleteAccount(reason: String): ApiResult<Unit> = ApiResult.Success(Unit)

    override suspend fun apiLogout(): ApiResult<Unit> = ApiResult.Success(Unit)

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

private class FakeSecureStorage : SecureStorage {
    private val data = mutableMapOf<String, Any>()

    override fun set(key: String, value: String): Boolean {
        data[key] = value
        return true
    }

    override fun set(key: String, value: Boolean): Boolean {
        data[key] = value
        return true
    }

    override fun string(forKey: String): String? = data[forKey] as? String

    override fun bool(forKey: String): Boolean? = data[forKey] as? Boolean

    override fun deleteObject(forKey: String): Boolean = data.remove(forKey) != null

    override fun clear(): Boolean {
        data.clear()
        return true
    }
}

private class FakeBiometricAuth : BiometricAuthGate {
    override suspend fun authenticate(reason: String): BiometricResult = BiometricResult.NotAvailable
}

private class NoOpFcmRegistrar : FcmRegistrar {
    override suspend fun registerIfAvailable() {}
    override suspend fun unregisterIfNeeded() {}
}

class LoginViewModelTest : TestDispatcher() {

    private fun createViewModel(repo: FakeAuthRepository = FakeAuthRepository()): LoginViewModel {
        val storage = FakeSecureStorage()
        return LoginViewModel(
            authRepository = repo,
            secureTokenManager = SecureTokenManager(storage),
            biometricAuth = FakeBiometricAuth(),
            fcmRegistration = NoOpFcmRegistrar()
        )
    }

    @Test
    fun initialUiStateHasDefaultValues() {
        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertEquals(false, state.isLoggedIn)
        assertEquals(false, state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun onEmailChangedUpdatesEmail() {
        val viewModel = createViewModel()

        viewModel.onIntent(LoginIntent.EmailChanged("test@example.com"))

        assertEquals("test@example.com", viewModel.uiState.value.email)
    }

    @Test
    fun onEmailChangedClearsErrorMessage() {
        val viewModel = createViewModel()

        viewModel.onIntent(LoginIntent.EmailChanged("test@example.com"))

        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun onPasswordChangedUpdatesPassword() {
        val viewModel = createViewModel()

        viewModel.onIntent(LoginIntent.PasswordChanged("secret"))

        assertEquals("secret", viewModel.uiState.value.password)
    }

    @Test
    fun onPasswordChangedClearsErrorMessage() {
        val viewModel = createViewModel()

        viewModel.onIntent(LoginIntent.PasswordChanged("secret"))

        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun loginWithBlankEmailShowsError() {
        val viewModel = createViewModel()

        viewModel.onIntent(LoginIntent.Login)

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals(false, viewModel.uiState.value.isLoggedIn)
    }

    @Test
    fun loginWithBlankPasswordShowsError() {
        val viewModel = createViewModel()

        viewModel.onIntent(LoginIntent.EmailChanged("test@example.com"))
        viewModel.onIntent(LoginIntent.Login)

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertEquals(false, viewModel.uiState.value.isLoggedIn)
    }

    @Test
    fun clearErrorResetsErrorMessage() {
        val viewModel = createViewModel()

        viewModel.onIntent(LoginIntent.ClearError)

        assertNull(viewModel.uiState.value.errorMessage)
    }
}
