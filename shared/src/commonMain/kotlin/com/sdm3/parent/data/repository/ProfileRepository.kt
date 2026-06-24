package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.ProfileApi
import com.sdm3.parent.data.remote.dto.ProfileDto
import com.sdm3.parent.domain.repository.ProfileRepositoryContract

class ProfileRepository(private val api: ProfileApi) : ProfileRepositoryContract {

    override suspend fun getProfile(): ApiResult<ProfileDto> {
        return try {
            api.getProfile()
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data profil"))
        }
    }

    override suspend fun updateProfile(name: String, phone: String?): ApiResult<ProfileDto> {
        return try {
            api.updateProfile(name, phone)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal memperbarui profil"))
        }
    }
}
