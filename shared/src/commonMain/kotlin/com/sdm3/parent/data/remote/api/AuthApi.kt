package com.sdm3.parent.data.remote.api

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.network.toApiResult
import com.sdm3.parent.data.remote.dto.ForgotPasswordRequest
import com.sdm3.parent.data.remote.dto.ForgotPasswordResponse
import com.sdm3.parent.data.remote.dto.LoginRequest
import com.sdm3.parent.data.remote.dto.LoginResponse
import com.sdm3.parent.data.remote.dto.ResetPasswordRequest
import com.sdm3.parent.data.remote.dto.ResetPasswordResponse
import com.sdm3.parent.data.remote.dto.UserDto
import com.sdm3.parent.data.remote.dto.VerifyOtpRequest
import com.sdm3.parent.data.remote.dto.VerifyOtpResponse
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class AuthApi(private val provider: HttpClientProvider) {

    suspend fun login(email: String, password: String): ApiResult<LoginResponse> {
        provider.client.get {
            url(Endpoints.SANCTUM_CSRF_COOKIE)
        }
        val response = provider.client.post {
            url(Endpoints.API_TOKEN)
            provider.applyAuthHeader(this)
            setBody(LoginRequest(email = email, password = password))
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun getUser(): ApiResult<UserDto> {
        val response = provider.client.get {
            url(Endpoints.API_USER)
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun getCsrfCookie() {
        provider.client.get {
            url(Endpoints.SANCTUM_CSRF_COOKIE)
        }
    }

    suspend fun requestOtp(email: String): ApiResult<ForgotPasswordResponse> {
        val response = provider.client.post {
            url(Endpoints.FORGOT_PASSWORD)
            setBody(ForgotPasswordRequest(email = email))
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun verifyOtp(email: String, otp: String): ApiResult<VerifyOtpResponse> {
        val response = provider.client.post {
            url(Endpoints.VERIFY_OTP)
            setBody(VerifyOtpRequest(email = email, otp = otp))
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun resetPassword(
        email: String,
        otp: String,
        password: String,
        passwordConfirmation: String
    ): ApiResult<ResetPasswordResponse> {
        val response = provider.client.post {
            url(Endpoints.RESET_PASSWORD)
            setBody(
                ResetPasswordRequest(
                    email = email,
                    otp = otp,
                    password = password,
                    passwordConfirmation = passwordConfirmation
                )
            )
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }
}
