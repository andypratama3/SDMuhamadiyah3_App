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

inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(data)
    return this
}

inline fun <T> ApiResult<T>.onError(action: (ApiError) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) action(error)
    return this
}
