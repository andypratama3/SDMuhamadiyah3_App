package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.di.DevMode
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.dummy.DummyDataProvider
import com.sdm3.parent.data.remote.api.ProfileApi
import com.sdm3.parent.data.remote.dto.ProfileDto
import com.sdm3.parent.domain.repository.ProfileRepositoryContract

class ProfileRepository(
    private val api: ProfileApi,
    private val cache: CacheDataSource,
) : ProfileRepositoryContract {

    override suspend fun getProfile(): ApiResult<ProfileDto> {
        return try {
            val result = api.getProfile()
            if (result is ApiResult.Success) cache.cacheProfile(result.data)
            result
        } catch (e: Exception) {
            val cached = cache.getProfile("1")
            if (cached != null) ApiResult.Success(cached)
            else if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummyProfile)
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data profil"))
        }
    }

    override suspend fun updateProfile(name: String, phone: String?): ApiResult<ProfileDto> {
        return try {
            val result = api.updateProfile(name, phone)
            if (result is ApiResult.Success) cache.cacheProfile(result.data)
            result
        } catch (e: Exception) {
            if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummyProfile)
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal memperbarui profil"))
        }
    }
}
