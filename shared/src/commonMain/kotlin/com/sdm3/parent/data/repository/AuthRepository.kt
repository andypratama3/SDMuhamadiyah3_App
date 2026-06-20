package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.AuthApi
import com.sdm3.parent.data.remote.dto.UserDto

class AuthRepository(private val api: AuthApi) {

    suspend fun login(email: String, password: String): ApiResult<UserDto> {
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

    suspend fun getAuthenticatedUser(): ApiResult<UserDto> {
        return try {
            api.getUser()
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data user"))
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return try {
            val result = api.getUser()
            result is ApiResult.Success
        } catch (_: Exception) {
            false
        }
    }

    suspend fun logout() {
        try {
            api.getCsrfCookie()
        } catch (_: Exception) { }
    }

    suspend fun requestOtp(email: String): ApiResult<String> {
        return try {
            when (val result = api.requestOtp(email)) {
                is ApiResult.Success -> ApiResult.Success(result.data.message)
                is ApiResult.Error -> result
            }
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengirim OTP"))
        }
    }

    suspend fun verifyOtp(email: String, otp: String): ApiResult<String> {
        return try {
            when (val result = api.verifyOtp(email, otp)) {
                is ApiResult.Success -> ApiResult.Success(result.data.message)
                is ApiResult.Error -> result
            }
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal verifikasi OTP"))
        }
    }

    suspend fun resetPassword(
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
