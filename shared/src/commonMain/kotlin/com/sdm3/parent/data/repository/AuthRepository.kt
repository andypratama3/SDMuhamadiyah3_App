package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.di.DevMode
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.dummy.DummyDataProvider
import com.sdm3.parent.data.remote.api.AuthApi
import com.sdm3.parent.data.remote.dto.UserDto
import com.sdm3.parent.domain.repository.AuthRepositoryContract

class AuthRepository(
    private val api: AuthApi,
    private val cache: CacheDataSource,
) : AuthRepositoryContract {

    override suspend fun login(email: String, password: String): ApiResult<UserDto> {
        return try {
            val result = api.login(email, password)
            when (result) {
                is ApiResult.Success -> ApiResult.Success(result.data.user)
                is ApiResult.Error -> result
            }
        } catch (e: Exception) {
            if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummyUser)
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal login"))
        }
    }

    override suspend fun getAuthenticatedUser(): ApiResult<UserDto> {
        return try {
            val result = api.getUser()
            if (result is ApiResult.Success) {
                val dto = result.data
                cache.cacheProfile(
                    com.sdm3.parent.data.remote.dto.ProfileDto(
                        id = dto.id, name = dto.name, email = dto.email,
                        phone = dto.phone, avatar = dto.avatar,
                    )
                )
            }
            result
        } catch (e: Exception) {
            if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummyUser)
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data user"))
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return try {
            val result = api.getUser()
            result is ApiResult.Success
        } catch (_: Exception) {
            DevMode.isEnabled || false
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
            if (DevMode.isEnabled) ApiResult.Success("Kode OTP telah dikirim ke email Anda")
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengirim OTP"))
        }
    }

    override suspend fun verifyOtp(email: String, otp: String): ApiResult<String> {
        return try {
            when (val result = api.verifyOtp(email, otp)) {
                is ApiResult.Success -> ApiResult.Success(result.data.message)
                is ApiResult.Error -> result
            }
        } catch (e: Exception) {
            if (DevMode.isEnabled) ApiResult.Success("OTP berhasil diverifikasi")
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal verifikasi OTP"))
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
            if (DevMode.isEnabled) ApiResult.Success("Password berhasil direset")
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal reset password"))
        }
    }
}
