package com.sdm3.parent.data.remote.api

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.network.toApiResult
import com.sdm3.parent.data.remote.dto.ProfileDto
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import kotlinx.serialization.SerialName
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

    suspend fun getMe(): ApiResult<ProfileDto> {
        val response = provider.client.get {
            url(Endpoints.PARENT_ME)
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun updateProfile(
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        password: String? = null,
        passwordConfirmation: String? = null
    ): ApiResult<ProfileDto> {
        val response = provider.client.patch {
            url(Endpoints.PARENT_PROFILE)
            provider.applyAuthHeader(this)
            setBody(
                UpdateProfileRequest(
                    name = name,
                    email = email,
                    phone = phone,
                    password = password,
                    passwordConfirmation = passwordConfirmation
                )
            )
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    @Serializable
    private data class UpdateProfileRequest(
        val name: String? = null,
        val email: String? = null,
        val phone: String? = null,
        val password: String? = null,
        @SerialName("password_confirmation")
        val passwordConfirmation: String? = null
    )
}
