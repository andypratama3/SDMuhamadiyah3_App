package com.sdm3.parent.core.test

import com.sdm3.parent.core.network.ApiResult

inline fun <reified T> mockSuccess(data: T): ApiResult<T> = ApiResult.Success(data)

inline fun <reified T> mockError(error: com.sdm3.parent.core.network.ApiError): ApiResult<T> =
    ApiResult.Error(error)
