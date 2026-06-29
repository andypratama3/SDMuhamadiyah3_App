package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.di.DevMode
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.dummy.DummyDataProvider
import com.sdm3.parent.data.remote.api.PaymentApi
import com.sdm3.parent.data.remote.dto.PaymentDto
import com.sdm3.parent.data.remote.dto.SnapTokenResponse
import com.sdm3.parent.data.remote.dto.StudentFeeDto
import com.sdm3.parent.domain.repository.PaymentRepositoryContract

class PaymentRepository(
    private val api: PaymentApi,
    private val cache: CacheDataSource,
) : PaymentRepositoryContract {

    override suspend fun getStudentFees(studentId: String): ApiResult<List<StudentFeeDto>> {
        return try {
            val result = api.getStudentFees(studentId)
            if (result is ApiResult.Success) cache.cacheFees(studentId, result.data)
            result
        } catch (e: Exception) {
            val cached = cache.getFees(studentId)
            if (cached.isNotEmpty()) ApiResult.Success(cached)
            else if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummyActiveFees)
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data tagihan"))
        }
    }

    override suspend fun getPayments(studentId: String, status: String?): ApiResult<List<PaymentDto>> {
        return try {
            val result = api.getPayments(studentId, status)
            if (result is ApiResult.Success) cache.cachePayments(studentId, result.data)
            result
        } catch (e: Exception) {
            val cached = cache.getPayments(studentId)
            if (cached.isNotEmpty()) ApiResult.Success(cached)
            else if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummyPaymentsForStudent(studentId, status))
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data pembayaran"))
        }
    }

    override suspend fun getSnapToken(paymentId: String): ApiResult<SnapTokenResponse> {
        return try {
            api.getSnapToken(paymentId)
        } catch (e: Exception) {
            if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummySnapToken)
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mendapatkan token pembayaran"))
        }
    }

    override suspend fun checkPaymentStatus(chargeId: String): ApiResult<PaymentDto> {
        return try {
            val result = api.checkPaymentStatus(chargeId)
            if (result is ApiResult.Success) cache.cachePayments("", listOf(result.data))
            result
        } catch (e: Exception) {
            if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummyCheckPaymentStatus(chargeId))
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengecek status pembayaran"))
        }
    }
}
