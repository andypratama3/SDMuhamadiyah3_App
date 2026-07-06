package com.sdm3.parent.data.remote.api

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.network.apiJson
import com.sdm3.parent.core.network.toApiResult
import com.sdm3.parent.data.remote.dto.ForgotPasswordRequest
import com.sdm3.parent.data.remote.dto.ForgotPasswordResponse
import com.sdm3.parent.data.remote.dto.LoginRequest
import com.sdm3.parent.data.remote.dto.LoginResponse
import com.sdm3.parent.core.network.sanitizeUserFacingMessage
import com.sdm3.parent.data.remote.dto.ResetPasswordRequest
import com.sdm3.parent.data.remote.dto.ResetPasswordResponse
import com.sdm3.parent.data.remote.dto.UserDto
import com.sdm3.parent.data.remote.dto.VerifyOtpRequest
import com.sdm3.parent.data.remote.dto.VerifyOtpResponse
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class AccountDeletionRequest(
    val reason: String
)

class AuthApi(private val provider: HttpClientProvider) {

    suspend fun login(email: String, password: String): ApiResult<LoginResponse> {
        val response = provider.client.post {
            url(Endpoints.API_TOKEN)
            setBody(LoginRequest(email = email, password = password, deviceName = "mobile"))
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.parseLoginResponse()
    }

    suspend fun getUser(): ApiResult<UserDto> {
        val response = provider.client.get {
            url(Endpoints.MOBILE_ME)
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun deleteAccount(reason: String): ApiResult<Unit> {
        val response = provider.client.delete {
            url(Endpoints.PARENT_ACCOUNT_DELETE)
            provider.applyAuthHeader(this)
            setBody(AccountDeletionRequest(reason = reason))
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun logout(): ApiResult<Unit> {
        val mobileResponse = provider.client.post {
            url(Endpoints.MOBILE_LOGOUT)
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(mobileResponse)
        if (mobileResponse.status.value != 404) {
            return mobileResponse.toApiResult()
        }

        val parentResponse = provider.client.post {
            url(Endpoints.LOGOUT)
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(parentResponse)
        return parentResponse.toApiResult()
    }

    suspend fun requestOtp(email: String): ApiResult<ForgotPasswordResponse> {
        val response = provider.client.post {
            url(Endpoints.FORGOT_PASSWORD)
            setBody(ForgotPasswordRequest(email = email))
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun verifyOtp(email: String, otp: String): ApiResult<VerifyOtpResponse> {
        val response = provider.client.post {
            url(Endpoints.VERIFY_OTP)
            setBody(VerifyOtpRequest(email = email, otp = otp))
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun resetPassword(
        email: String,
        otp: String,
        password: String,
        passwordConfirmation: String
    ): ApiResult<ResetPasswordResponse> {
        val response = provider.client.post {
            url(Endpoints.RESET_PASSWORD)
            setBody(
                ResetPasswordRequest(
                    email = email,
                    otp = otp,
                    password = password,
                    passwordConfirmation = passwordConfirmation
                )
            )
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }
}

private suspend inline fun HttpResponse.parseLoginResponse(): ApiResult<LoginResponse> {
    val text = body<String>()
    if (status.value == 419) return ApiResult.Error(ApiError.SessionExpired)
    return when (status) {
        HttpStatusCode.OK, HttpStatusCode.Created -> parseDirectOrEnvelope(text)
        HttpStatusCode.Unauthorized -> ApiResult.Error(ApiError.Unauthorized("Sesi tidak valid, silakan login kembali."))
        else -> toApiResult()
    }
}

private inline fun <reified T> parseDirectOrEnvelope(text: String): ApiResult<T> {
    return try {
        val root = apiJson.parseToJsonElement(text).jsonObject
        val data = root["data"]
        when {
            data != null && data !is JsonNull -> {
                ApiResult.Success(apiJson.decodeFromJsonElement(serializer<T>(), data))
            }
            else -> {
                ApiResult.Success(apiJson.decodeFromString<T>(text))
            }
        }
    } catch (e: Exception) {
        ApiResult.Error(ApiError.Unknown(sanitizeUserFacingMessage(null)))
    }
}
