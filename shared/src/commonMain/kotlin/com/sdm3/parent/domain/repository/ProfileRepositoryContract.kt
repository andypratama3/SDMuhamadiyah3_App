package com.sdm3.parent.domain.repository

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.ProfileDto

interface ProfileRepositoryContract {
    suspend fun getProfile(): ApiResult<ProfileDto>
    suspend fun updateProfile(
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        password: String? = null,
        passwordConfirmation: String? = null
    ): ApiResult<ProfileDto>
    suspend fun uploadAvatar(
        bytes: ByteArray,
        fileName: String,
        mimeType: String,
    ): ApiResult<ProfileDto>
}
