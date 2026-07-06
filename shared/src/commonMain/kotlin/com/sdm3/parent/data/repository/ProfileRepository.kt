package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.safeApiCall
import com.sdm3.parent.core.network.safeApiCallWithCache
import com.sdm3.parent.core.security.SecureTokenManager
import com.sdm3.parent.data.remote.api.ProfileApi
import com.sdm3.parent.data.remote.dto.ProfileDto
import com.sdm3.parent.domain.repository.ProfileRepositoryContract

class ProfileRepository(
    private val api: ProfileApi,
    private val cache: CacheDataSource,
    private val secureTokenManager: SecureTokenManager,
) : ProfileRepositoryContract {

    override suspend fun getProfile(): ApiResult<ProfileDto> =
        safeApiCallWithCache(
            fallback = "Gagal mengambil data profil",
            block = {
                when (val result = api.getProfile()) {
                    is ApiResult.Success -> {
                        cache.cacheProfile(result.data)
                        result
                    }
                    is ApiResult.Error -> result
                }
            },
            cacheFallback = {
                secureTokenManager.getUserId()?.let { cache.getProfile(it) }
            },
        )

    override suspend fun updateProfile(
        name: String?,
        email: String?,
        phone: String?,
        password: String?,
        passwordConfirmation: String?,
    ): ApiResult<ProfileDto> = safeApiCall("Gagal memperbarui profil") {
        when (val result = api.updateProfile(name, email, phone, password, passwordConfirmation)) {
            is ApiResult.Success -> {
                cache.cacheProfile(result.data)
                result
            }
            is ApiResult.Error -> result
        }
    }

    override suspend fun uploadAvatar(
        bytes: ByteArray,
        fileName: String,
        mimeType: String,
    ): ApiResult<ProfileDto> = safeApiCall("Gagal mengunggah foto profil") {
        when (val result = api.uploadAvatar(bytes, fileName, mimeType)) {
            is ApiResult.Success -> {
                cache.cacheProfile(result.data)
                result
            }
            is ApiResult.Error -> result
        }
    }
}
