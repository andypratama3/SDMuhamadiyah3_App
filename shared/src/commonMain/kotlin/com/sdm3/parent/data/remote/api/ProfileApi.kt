package com.sdm3.parent.data.remote.api

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.network.toApiResult
import com.sdm3.parent.data.remote.dto.ProfileDto
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import kotlinx.serialization.Serializable

class ProfileApi(private val provider: HttpClientProvider) {

    suspend fun getProfile(): ApiResult<ProfileDto> {
        val response = provider.client.get {
            url(Endpoints.PARENT_PROFILE)
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun updateProfile(name: String, phone: String?): ApiResult<ProfileDto> {
        val response = provider.client.patch {
            url(Endpoints.PARENT_PROFILE)
            provider.applyAuthHeader(this)
            setBody(UpdateProfileRequest(name = name, phone = phone))
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    @Serializable
    private data class UpdateProfileRequest(
        val name: String,
        val phone: String? = null
    )
}
