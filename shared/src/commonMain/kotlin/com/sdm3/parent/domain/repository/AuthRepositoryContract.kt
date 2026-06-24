package com.sdm3.parent.domain.repository

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.UserDto

interface AuthRepositoryContract {
    suspend fun login(email: String, password: String): ApiResult<UserDto>
    suspend fun getAuthenticatedUser(): ApiResult<UserDto>
    suspend fun isLoggedIn(): Boolean
    suspend fun logout()
    suspend fun requestOtp(email: String): ApiResult<String>
    suspend fun verifyOtp(email: String, otp: String): ApiResult<String>
    suspend fun resetPassword(
        email: String, otp: String, password: String, passwordConfirmation: String
    ): ApiResult<String>
}
