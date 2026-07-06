package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.safeApiCall
import com.sdm3.parent.core.security.SecureTokenManager
import com.sdm3.parent.data.remote.api.AuthApi
import com.sdm3.parent.data.remote.dto.ProfileDto
import com.sdm3.parent.data.remote.dto.UserDto
import com.sdm3.parent.domain.model.RoleContext
import com.sdm3.parent.domain.repository.AuthRepositoryContract

class AuthRepository(
    private val api: AuthApi,
    private val cache: CacheDataSource,
    private val secureTokenManager: SecureTokenManager,
) : AuthRepositoryContract {

    override suspend fun login(email: String, password: String): ApiResult<UserDto> =
        safeApiCall("Gagal login") {
            when (val result = api.login(email, password)) {
                is ApiResult.Success -> {
                    persistAuthenticatedUser(result.data.user, result.data.token)
                    ApiResult.Success(result.data.user)
                }
                is ApiResult.Error -> result
            }
        }

    override suspend fun getAuthenticatedUser(): ApiResult<UserDto> =
        safeApiCall("Gagal mengambil data user") {
            when (val result = api.getUser()) {
                is ApiResult.Success -> {
                    persistAuthenticatedUser(result.data, secureTokenManager.getBearerToken().orEmpty())
                    result
                }
                is ApiResult.Error -> result
            }
        }

    override fun resolveStoredRoleContext(): RoleContext {
        val userId = secureTokenManager.getUserId()
        if (userId != null) {
            cache.getProfile(userId)?.let { profile ->
                if (!profile.role.isNullOrBlank() || profile.roles.isNotEmpty()) {
                    return RoleContext.fromProfile(profile)
                }
            }
        }
        return secureTokenManager.getRoleContext()
    }

    override suspend fun isLoggedIn(): Boolean =
        when (val result = safeApiCall("Gagal memverifikasi sesi") { api.getUser() }) {
            is ApiResult.Success -> true
            is ApiResult.Error -> false
        }

    override suspend fun apiLogout(): ApiResult<Unit> =
        safeApiCall("Gagal logout") { api.logout() }

    override suspend fun deleteAccount(reason: String): ApiResult<Unit> =
        safeApiCall("Gagal menghapus akun") { api.deleteAccount(reason) }

    override suspend fun clearLocalSession() {
        secureTokenManager.clearAllSecureData()
        cache.clearAll()
    }

    override suspend fun logout() {
        clearLocalSession()
    }

    override suspend fun requestOtp(email: String): ApiResult<String> =
        safeApiCall("Gagal mengirim OTP") {
            when (val result = api.requestOtp(email)) {
                is ApiResult.Success -> ApiResult.Success(result.data.message)
                is ApiResult.Error -> result
            }
        }

    override suspend fun verifyOtp(email: String, otp: String): ApiResult<String> =
        safeApiCall("Gagal verifikasi OTP") {
            when (val result = api.verifyOtp(email, otp)) {
                is ApiResult.Success -> ApiResult.Success(result.data.message)
                is ApiResult.Error -> result
            }
        }

    override suspend fun resetPassword(
        email: String,
        otp: String,
        password: String,
        passwordConfirmation: String,
    ): ApiResult<String> = safeApiCall("Gagal reset password") {
        when (val result = api.resetPassword(email, otp, password, passwordConfirmation)) {
            is ApiResult.Success -> ApiResult.Success(result.data.message)
            is ApiResult.Error -> result
        }
    }

    private fun persistAuthenticatedUser(user: UserDto, token: String) {
        if (token.isNotBlank()) {
            secureTokenManager.saveBearerToken(token)
        }
        val roleContext = RoleContext.fromUser(user)
        secureTokenManager.saveRoleContext(roleContext)
        secureTokenManager.saveUserId(user.id)
        if (roleContext.isTeacherOnly()) {
            secureTokenManager.setOnboardingCompleted(true)
        }
        cache.cacheProfile(user.toProfileDto())
    }

    private fun UserDto.toProfileDto() = ProfileDto(
        id = id,
        name = name,
        email = email,
        phone = phone,
        avatar = avatar,
        role = role,
        roles = roles,
    )
}
