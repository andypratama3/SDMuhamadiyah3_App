package com.sdm3.parent.feature.auth

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.test.TestDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class VerifikasiOtpViewModelTest : TestDispatcher() {

    @Test
    fun initialUiStateHasDefaultValues() {
        val repo = FakeAuthRepository()
        val viewModel = VerifikasiOtpViewModel(repo)

        val state = viewModel.uiState.value
        assertEquals("", state.email)
        assertEquals("", state.otpCode)
        assertEquals("", state.newPassword)
        assertEquals("", state.newPasswordConfirmation)
        assertEquals(false, state.isPasswordVisible)
        assertEquals(60, state.countdownSeconds)
        assertEquals(false, state.canResend)
        assertEquals(OtpStep.REQUEST_OTP, state.step)
        assertNull(state.resetSuccessMessage)
        assertEquals(false, state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun setEmailUpdatesState() {
        val repo = FakeAuthRepository()
        val viewModel = VerifikasiOtpViewModel(repo)

        viewModel.setEmail("parent@example.com")

        assertEquals("parent@example.com", viewModel.uiState.value.email)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun updateOtpCodeDigitsOnly() {
        val repo = FakeAuthRepository()
        val viewModel = VerifikasiOtpViewModel(repo)

        viewModel.updateOtpCode("123a")
        assertEquals("", viewModel.uiState.value.otpCode)

        viewModel.updateOtpCode("123456")
        assertEquals("123456", viewModel.uiState.value.otpCode)

        viewModel.updateOtpCode("1234567") // more than 6 digits
        assertEquals("123456", viewModel.uiState.value.otpCode)
    }

    @Test
    fun updateNewPasswordUpdatesState() {
        val repo = FakeAuthRepository()
        val viewModel = VerifikasiOtpViewModel(repo)

        viewModel.updateNewPassword("pass123")
        assertEquals("pass123", viewModel.uiState.value.newPassword)
    }

    @Test
    fun updateNewPasswordConfirmationUpdatesState() {
        val repo = FakeAuthRepository()
        val viewModel = VerifikasiOtpViewModel(repo)

        viewModel.updateNewPasswordConfirmation("pass123")
        assertEquals("pass123", viewModel.uiState.value.newPasswordConfirmation)
    }

    @Test
    fun togglePasswordVisibilityChangesState() {
        val repo = FakeAuthRepository()
        val viewModel = VerifikasiOtpViewModel(repo)

        assertEquals(false, viewModel.uiState.value.isPasswordVisible)
        viewModel.togglePasswordVisibility()
        assertEquals(true, viewModel.uiState.value.isPasswordVisible)
    }

    @Test
    fun requestOtpWithBlankEmailShowsError() {
        val repo = FakeAuthRepository()
        val viewModel = VerifikasiOtpViewModel(repo)

        viewModel.requestOtp()

        assertEquals("Email harus diisi", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun requestOtpSuccessTransitionsStepAndStartsCountdown() {
        val repo = FakeAuthRepository()
        val viewModel = VerifikasiOtpViewModel(repo)

        viewModel.setEmail("test@example.com")
        viewModel.requestOtp()

        testDispatcher.scheduler.runCurrent()

        assertEquals(OtpStep.VERIFY_OTP, viewModel.uiState.value.step)
        assertEquals(60, viewModel.uiState.value.countdownSeconds)

        // Advance time to verify countdown decreases
        testDispatcher.scheduler.advanceTimeBy(2000L)
        assertTrue(viewModel.uiState.value.countdownSeconds < 60)
    }

    @Test
    fun verifyOtpWithShortCodeShowsError() {
        val repo = FakeAuthRepository()
        val viewModel = VerifikasiOtpViewModel(repo)

        viewModel.setEmail("test@example.com")
        viewModel.updateOtpCode("123")
        viewModel.verifyOtp()

        assertEquals("Masukkan 6 digit kode OTP", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun verifyOtpSuccessTransitionsStep() {
        val repo = FakeAuthRepository()
        val viewModel = VerifikasiOtpViewModel(repo)

        viewModel.setEmail("test@example.com")
        viewModel.updateOtpCode("123456")
        viewModel.verifyOtp()

        testDispatcher.scheduler.runCurrent()

        assertEquals(OtpStep.RESET_PASSWORD, viewModel.uiState.value.step)
    }

    @Test
    fun resetPasswordWithShortPasswordShowsError() {
        val repo = FakeAuthRepository()
        val viewModel = VerifikasiOtpViewModel(repo)

        viewModel.updateNewPassword("123")
        viewModel.resetPassword()

        assertEquals("Password minimal 6 karakter", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun resetPasswordMismatchShowsError() {
        val repo = FakeAuthRepository()
        val viewModel = VerifikasiOtpViewModel(repo)

        viewModel.updateNewPassword("password123")
        viewModel.updateNewPasswordConfirmation("password456")
        viewModel.resetPassword()

        assertEquals("Password tidak cocok", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun resetPasswordSuccessSetsSuccessMessage() {
        val repo = FakeAuthRepository()
        val viewModel = VerifikasiOtpViewModel(repo)

        viewModel.setEmail("test@example.com")
        viewModel.updateOtpCode("123456")
        viewModel.updateNewPassword("password123")
        viewModel.updateNewPasswordConfirmation("password123")
        viewModel.resetPassword()

        testDispatcher.scheduler.runCurrent()

        assertEquals("Password reset", viewModel.uiState.value.resetSuccessMessage)
    }
}
