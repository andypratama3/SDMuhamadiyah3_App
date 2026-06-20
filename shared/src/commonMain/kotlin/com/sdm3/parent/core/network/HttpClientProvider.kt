package com.sdm3.parent.core.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.request.HttpRequestBuilder

class HttpClientProvider(
    private val baseUrl: String,
    private val tokenProvider: suspend () -> String?,
    private val onSessionExpired: suspend () -> Unit,
    private val certificatePins: List<String> = emptyList()
) {
    val client: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                explicitNulls = false
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
        }
        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) {
                    if (!message.contains("Authorization", ignoreCase = true) &&
                        !message.contains("password", ignoreCase = true)
                    ) {
                        println("[SDM3] $message")
                    }
                }
            }
        }
        defaultRequest {
            url(baseUrl)
            contentType(ContentType.Application.Json)
            header("Accept", "application/json")
            header("X-Requested-With", "XMLHttpRequest")
        }
        engine {
            applyPlatformSslPinning(certificatePins)
        }
    }

    internal suspend fun applyAuthHeader(builder: HttpRequestBuilder) {
        val token = tokenProvider()
        if (token != null) {
            builder.header("Authorization", "Bearer $token")
        }
    }

    internal suspend fun handleSessionExpiredIfNeeded(response: HttpResponse) {
        if (response.status.value == 419) {
            onSessionExpired()
        }
    }
}

suspend inline fun <reified T> HttpResponse.toApiResult(): ApiResult<T> {
    return when (status) {
        HttpStatusCode.OK, HttpStatusCode.Created -> {
            ApiResult.Success(body())
        }
        HttpStatusCode.Unauthorized -> ApiResult.Error(ApiError.Unauthorized("Sesi tidak valid, silakan login kembali."))
        HttpStatusCode.Forbidden -> ApiResult.Error(ApiError.Forbidden("Anda tidak memiliki akses ke data ini."))
        HttpStatusCode.NotFound -> ApiResult.Error(ApiError.NotFound)
        HttpStatusCode(419, "") -> ApiResult.Error(ApiError.SessionExpired)
        HttpStatusCode.UnprocessableEntity -> {
            val body: LaravelValidationErrorDto = body()
            ApiResult.Error(ApiError.Validation(body.errors))
        }
        HttpStatusCode.TooManyRequests -> {
            val retryAfter = headers["Retry-After"]?.toIntOrNull()
            ApiResult.Error(ApiError.RateLimited(retryAfter))
        }
        else -> {
            if (status.value >= 500) {
                ApiResult.Error(ApiError.ServerError(status.value))
            } else {
                ApiResult.Error(ApiError.Unknown("Terjadi kesalahan tidak terduga (${status.value})"))
            }
        }
    }
}

@kotlinx.serialization.Serializable
data class LaravelValidationErrorDto(
    val message: String,
    val errors: Map<String, List<String>>
)
