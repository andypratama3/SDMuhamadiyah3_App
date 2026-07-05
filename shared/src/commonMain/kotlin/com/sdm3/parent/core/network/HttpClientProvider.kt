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
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer
import io.ktor.client.request.HttpRequestBuilder
import com.sdm3.parent.core.event.SessionEventBus
import com.sdm3.parent.isDebugBuild

class HttpClientProvider(
    private val baseUrl: String,
    private val tokenProvider: suspend () -> String?,
    private val onSessionExpired: suspend () -> Unit,
    private val certificatePins: List<String> = emptyList(),
    private val enableLogging: Boolean = false
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
            level = if (enableLogging) LogLevel.INFO else LogLevel.NONE
            logger = object : Logger {
                override fun log(message: String) {
                    if (!enableLogging) return
                    if (message.contains("CancellationException", ignoreCase = true) ||
                        message.contains("Job was cancelled", ignoreCase = true)
                    ) {
                        return
                    }
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
        if (response.status.value == 419 || response.status.value == 401) {
            onSessionExpired()
            SessionEventBus.emit()
        }
    }
}

@PublishedApi internal val apiJson = Json { ignoreUnknownKeys = true; isLenient = true; explicitNulls = false; coerceInputValues = true }

suspend inline fun <reified T> HttpResponse.toApiResult(): ApiResult<T> {
    val text = body<String>()
    val root = try {
        apiJson.parseToJsonElement(text).jsonObject
    } catch (_: Exception) {
        return try {
            ApiResult.Success(apiJson.decodeFromString<T>(text))
        } catch (_: Exception) {
            ApiResult.Error(ApiError.Unknown("Gagal memproses respons server"))
        }
    }

    val message = (root["message"] as? JsonPrimitive)?.content

    if (status.value == 419) {
        return ApiResult.Error(ApiError.SessionExpired)
    }

    return when (status) {
        HttpStatusCode.OK, HttpStatusCode.Created -> {
            val data = root["data"]
            if (data == null || data is JsonNull) {
                return if (T::class == Unit::class) {
                    @Suppress("UNCHECKED_CAST")
                    ApiResult.Success(Unit as T)
                } else {
                    ApiResult.Error(ApiError.Unknown("Data kosong"))
                }
            }
            try {
                ApiResult.Success(apiJson.decodeFromJsonElement(serializer<T>(), data))
            } catch (e: Exception) {
                // Backend void endpoints mengembalikan data: {} untuk operasi sukses tanpa payload.
                if (T::class == Unit::class && data is JsonObject) {
                    @Suppress("UNCHECKED_CAST")
                    return ApiResult.Success(Unit as T)
                }
                if (isDebugBuild()) {
                    println("[SDM3] Deserialization error for ${T::class.simpleName}: ${e.message}")
                }
                ApiResult.Error(ApiError.Unknown("Gagal memproses data: ${e.message}"))
            }
        }
        HttpStatusCode.Unauthorized -> ApiResult.Error(ApiError.Unauthorized(message ?: "Sesi tidak valid, silakan login kembali."))
        HttpStatusCode.Forbidden -> ApiResult.Error(ApiError.Forbidden(message ?: "Anda tidak memiliki akses ke data ini."))
        HttpStatusCode.NotFound -> ApiResult.Error(ApiError.NotFound)
        HttpStatusCode.UnprocessableEntity -> {
            val errors = try {
                val errorsElement = root["data"]?.jsonObject?.get("errors")
                if (errorsElement != null) {
                    apiJson.decodeFromJsonElement(serializer<Map<String, List<String>>>(), errorsElement)
                } else emptyMap()
            } catch (_: Exception) {
                emptyMap()
            }
            ApiResult.Error(ApiError.Validation(errors))
        }
        HttpStatusCode.TooManyRequests -> {
            val retryAfter = headers["Retry-After"]?.toIntOrNull()
            ApiResult.Error(ApiError.RateLimited(retryAfter))
        }
        else -> {
            if (status.value >= 500) {
                ApiResult.Error(ApiError.ServerError(status.value))
            } else {
                ApiResult.Error(ApiError.Unknown(message ?: "Terjadi kesalahan tidak terduga (${status.value})"))
            }
        }
    }
}
