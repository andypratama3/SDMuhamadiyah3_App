package com.sdm3.parent.domain.repository

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.ProfileDto

interface ProfileRepositoryContract {
    suspend fun getProfile(): ApiResult<ProfileDto>
    suspend fun updateProfile(name: String, phone: String?): ApiResult<ProfileDto>
}
