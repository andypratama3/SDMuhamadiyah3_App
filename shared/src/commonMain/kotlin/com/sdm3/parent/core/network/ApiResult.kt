package com.sdm3.parent.core.network

sealed class ApiError {
    data object NoInternet : ApiError()
    data object Timeout : ApiError()
    data class Unauthorized(val message: String) : ApiError()
    data class Forbidden(val message: String) : ApiError()
    data object NotFound : ApiError()
    data object SessionExpired : ApiError()
    data class Validation(val fieldErrors: Map<String, List<String>>) : ApiError()
    data class RateLimited(val retryAfterSeconds: Int?) : ApiError()
    data class ServerError(val code: Int) : ApiError()
    data class Unknown(val message: String) : ApiError()
}

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val error: ApiError) : ApiResult<Nothing>()
}

fun ApiError.userMessage(): String = when (this) {
    ApiError.NoInternet -> "Tidak ada koneksi internet. Periksa jaringan Anda."
    ApiError.Timeout -> "Permintaan terlalu lama. Silakan coba lagi."
    is ApiError.Unauthorized -> "Sesi Anda tidak valid. Silakan masuk kembali."
    is ApiError.Forbidden -> "Anda tidak memiliki akses untuk data ini."
    ApiError.NotFound -> "Data tidak ditemukan."
    ApiError.SessionExpired -> "Sesi Anda telah berakhir. Silakan masuk kembali."
    is ApiError.Validation -> "Data yang dimasukkan tidak valid."
    is ApiError.RateLimited -> "Terlalu banyak percobaan. Silakan coba lagi nanti."
    is ApiError.ServerError -> "Server sedang bermasalah. Silakan coba lagi."
    is ApiError.Unknown -> sanitizeUserFacingMessage(message)
}

suspend inline fun <T> safeApiCall(
    fallback: String,
    crossinline block: suspend () -> ApiResult<T>,
): ApiResult<T> = try {
    block()
} catch (e: Exception) {
    ApiResult.Error(e.toApiError(fallback))
}

suspend inline fun <T> safeApiCallWithCache(
    fallback: String,
    crossinline block: suspend () -> ApiResult<T>,
    noinline cacheFallback: () -> T?,
): ApiResult<T> = try {
    when (val result = block()) {
        is ApiResult.Success -> result
        is ApiResult.Error -> {
            val cached = cacheFallback()
            if (cached != null) ApiResult.Success(cached) else result
        }
    }
} catch (e: Exception) {
    val cached = cacheFallback()
    if (cached != null) ApiResult.Success(cached)
    else ApiResult.Error(e.toApiError(fallback))
}

inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(data)
    return this
}

inline fun <T> ApiResult<T>.onError(action: (ApiError) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) action(error)
    return this
}
