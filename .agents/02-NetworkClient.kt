/*
 * SDM3 Parent Portal — Network Layer (Ktor + Laravel Sanctum)
 *
 * Mengikuti aturan ketat Section 21 blueprint:
 *  - Semua operasi network dibungkus Result<T>, tidak ada exception mentah yang lolos
 *  - Tidak ada !! (double-bang)
 *  - Tidak ada log untuk: password, token, OTP, nomor HP, email
 *  - Error 419 (session expired Laravel) WAJIB memicu redirect ke Login
 */
package com.sdm3.parent.core.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Representasi error domain yang SUDAH ramah ditampilkan ke user.
 * Jangan pernah lempar [Throwable] mentah ke ViewModel — selalu mapping ke sini.
 */
sealed class ApiError {
    data object NoInternet : ApiError()
    data object Timeout : ApiError()

    /** HTTP 401 — kredensial salah/tidak terkirim. */
    data class Unauthorized(val message: String) : ApiError()

    /** HTTP 403 — role tidak punya akses (contoh: bukan role "parent"). */
    data class Forbidden(val message: String) : ApiError()

    data object NotFound : ApiError()

    /**
     * HTTP 419 — sesi Laravel (Sanctum SPA cookie) sudah expired.
     * ViewModel/Repository WAJIB menangkap ini dan memicu navigasi ke Login,
     * TANPA menampilkan data lama di layar (cegah data leak — Section 21).
     */
    data object SessionExpired : ApiError()

    /** HTTP 422 — validasi gagal. [fieldErrors] sesuai format Laravel: { field: [msg, ...] } */
    data class Validation(val fieldErrors: Map<String, List<String>>) : ApiError()

    /** HTTP 429 — rate limited (contoh: brute force login). */
    data class RateLimited(val retryAfterSeconds: Int?) : ApiError()

    data class ServerError(val code: Int) : ApiError()
    data class Unknown(val message: String) : ApiError()
}

/**
 * Wrapper hasil operasi network. Dipakai di SEMUA layer Repository — jangan
 * buat custom wrapper lain (aturan Architect Agent, Section 22).
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val error: ApiError) : ApiResult<Nothing>()
}

inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(data)
    return this
}

inline fun <T> ApiResult<T>.onError(action: (ApiError) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) action(error)
    return this
}

/**
 * Menyediakan satu instance [HttpClient] yang dipakai bersama oleh SEMUA
 * Repository (jangan bikin HttpClient baru per-feature — aturan anti-duplikasi
 * Section 21).
 *
 * @param baseUrl contoh: "https://admin.sdm3.sch.id"
 * @param tokenProvider dipanggil setiap request untuk mengambil Bearer token
 *   dari penyimpanan terenkripsi (lihat SecureTokenManager). Hanya dipakai
 *   sebagai FALLBACK — jalur utama tetap cookie-based Sanctum SPA.
 * @param onSessionExpired dipanggil otomatis setiap kali server membalas 419,
 *   supaya UI bisa langsung redirect ke Login tanpa setiap Repository harus
 *   cek manual.
 */
class HttpClientProvider(
    private val baseUrl: String,
    private val tokenProvider: suspend () -> String?,
    private val onSessionExpired: suspend () -> Unit
) {
    val client: HttpClient = HttpClient {
        // Cookie jar otomatis — diperlukan untuk Sanctum SPA auth (CSRF cookie +
        // session cookie). Tanpa ini, login cookie-based tidak akan tersimpan.
        install(HttpCookies)

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true // Laravel boleh nambah field baru tanpa break client
                    isLenient = true
                    explicitNulls = false
                }
            )
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
        }

        install(Logging) {
            level = LogLevel.INFO // INFO saja di production, JANGAN LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    // PENTING: filter manual supaya Authorization header / body
                    // login tidak pernah tercetak ke Napier/Logcat.
                    if (!message.contains("Authorization", ignoreCase = true) &&
                        !message.contains("password", ignoreCase = true)
                    ) {
                        co.touchlab.kermit.Logger.d { message }
                    }
                }
            }
        }

        defaultRequest {
            url(baseUrl)
            contentType(ContentType.Application.Json)
            header("Accept", "application/json")
            header("X-Requested-With", "XMLHttpRequest") // diharapkan Laravel untuk 419 vs redirect HTML
        }
    }

    /**
     * Dipanggil oleh AuthInterceptor sebelum setiap request yang butuh auth.
     * Bearer token hanya dikirim sebagai fallback — request cookie-based tidak
     * butuh ini karena HttpCookies plugin sudah otomatis mengirim cookie.
     */
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

/**
 * Mapping HttpResponse -> ApiResult<T>. Panggil dari setiap RemoteDataSource:
 *
 * ```
 * val response = httpClient.get("/api/parent/students")
 * return response.toApiResult<List<StudentDto>>()
 * ```
 */
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
