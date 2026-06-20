package com.sdm3.parent.core.network

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ApiResultTest {

    @Test
    fun onSuccessInvokesActionForSuccessResult() {
        var invoked = false
        val result: ApiResult<String> = ApiResult.Success("hello")

        result.onSuccess { data ->
            invoked = true
            assertEquals("hello", data)
        }

        assertTrue(invoked)
    }

    @Test
    fun onSuccessDoesNotInvokeActionForErrorResult() {
        var invoked = false
        val result: ApiResult<String> = ApiResult.Error(ApiError.NotFound)

        result.onSuccess { invoked = true }

        assertTrue(!invoked)
    }

    @Test
    fun onErrorInvokesActionForErrorResult() {
        var invoked = false
        val expectedError = ApiError.NotFound
        val result: ApiResult<String> = ApiResult.Error(expectedError)

        result.onError { error ->
            invoked = true
            assertEquals(expectedError, error)
        }

        assertTrue(invoked)
    }

    @Test
    fun onErrorDoesNotInvokeActionForSuccessResult() {
        var invoked = false
        val result: ApiResult<String> = ApiResult.Success("data")

        result.onError { invoked = true }

        assertTrue(!invoked)
    }

    @Test
    fun onSuccessReturnsSameResultToAllowChaining() {
        val result: ApiResult<String> = ApiResult.Success("data")

        val chained = result.onSuccess {}

        assertEquals(result, chained)
    }

    @Test
    fun onErrorReturnsSameResultToAllowChaining() {
        val result: ApiResult<String> = ApiResult.Error(ApiError.Timeout)

        val chained = result.onError {}

        assertEquals(result, chained)
    }

    @Test
    fun successResultContainsCorrectData() {
        val result = ApiResult.Success(42)

        assertTrue(result is ApiResult.Success)
        assertEquals(42, result.data)
    }

    @Test
    fun errorResultContainsCorrectError() {
        val result = ApiResult.Error(ApiError.NoInternet)

        assertTrue(result is ApiResult.Error)
        assertEquals(ApiError.NoInternet, result.error)
    }

    @Test
    fun successResultHasNullDataForNothingType() {
        val result = ApiResult.Success(Unit)

        assertTrue(result is ApiResult.Success)
        assertEquals(Unit, result.data)
    }

    @Test
    fun errorResultWithUnauthorizedError() {
        val result = ApiResult.Error(ApiError.Unauthorized("invalid token"))

        assertTrue(result is ApiResult.Error)
        val error = (result as ApiResult.Error).error
        assertTrue(error is ApiError.Unauthorized)
        assertEquals("invalid token", (error as ApiError.Unauthorized).message)
    }

    @Test
    fun errorResultWithValidationError() {
        val fieldErrors = mapOf("email" to listOf("Email is required"))
        val result = ApiResult.Error(ApiError.Validation(fieldErrors))

        assertTrue(result is ApiResult.Error)
        val error = (result as ApiResult.Error).error
        assertTrue(error is ApiError.Validation)
        assertEquals(fieldErrors, (error as ApiError.Validation).fieldErrors)
    }

    @Test
    fun successResultOnNothingType() {
        val result: ApiResult<Nothing?> = ApiResult.Success(null)

        result.onSuccess { data ->
            assertNull(data)
        }
    }
}
