package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.AuthApi
import com.sdm3.parent.data.remote.dto.UserDto
import com.sdm3.parent.domain.repository.AuthRepositoryContract

class AuthRepository(private val api: AuthApi) : AuthRepositoryContract {

    override suspend fun login(email: String, password: String): ApiResult<UserDto> {
        return try {
            val result = api.login(email, password)
            when (result) {
                is ApiResult.Success -> ApiResult.Success(result.data.user)
                is ApiResult.Error -> result
            }
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal login"))
        }
    }

    override suspend fun getAuthenticatedUser(): ApiResult<UserDto> {
        return try {
            api.getUser()
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data user"))
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return try {
            val result = api.getUser()
            result is ApiResult.Success
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun logout() {
        try {
            api.getCsrfCookie()
        } catch (_: Exception) { }
    }

    override suspend fun requestOtp(email: String): ApiResult<String> {
        return try {
            when (val result = api.requestOtp(email)) {
                is ApiResult.Success -> ApiResult.Success(result.data.message)
                is ApiResult.Error -> result
            }
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengirim OTP"))
        }
    }

    override suspend fun verifyOtp(email: String, otp: String): ApiResult<String> {
        return try {
            when (val result = api.verifyOtp(email, otp)) {
                is ApiResult.Success -> ApiResult.Success(result.data.message)
                is ApiResult.Error -> result
            }
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal verifikasi OTP"))
        }
    }

    override suspend fun resetPassword(
        email: String,
        otp: String,
        password: String,
        passwordConfirmation: String
    ): ApiResult<String> {
        return try {
            when (val result = api.resetPassword(email, otp, password, passwordConfirmation)) {
                is ApiResult.Success -> ApiResult.Success(result.data.message)
                is ApiResult.Error -> result
            }
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal reset password"))
        }
    }
}
