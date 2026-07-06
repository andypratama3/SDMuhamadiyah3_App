package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.safeApiCall
import com.sdm3.parent.core.network.safeApiCallWithCache
import com.sdm3.parent.data.remote.api.PaymentApi
import com.sdm3.parent.data.remote.dto.PaymentDto
import com.sdm3.parent.data.remote.dto.PaymentMethodDto
import com.sdm3.parent.data.remote.dto.SnapTokenResponse
import com.sdm3.parent.data.remote.dto.StudentFeeDto
import com.sdm3.parent.domain.repository.PaymentRepositoryContract

class PaymentRepository(
    private val api: PaymentApi,
    private val cache: CacheDataSource,
) : PaymentRepositoryContract {

    override suspend fun getStudentFees(studentId: String): ApiResult<List<StudentFeeDto>> =
        safeApiCallWithCache(
            fallback = "Gagal mengambil data tagihan",
            block = {
                when (val result = api.getStudentFees(studentId)) {
                    is ApiResult.Success -> {
                        cache.cacheFees(studentId, result.data)
                        result
                    }
                    is ApiResult.Error -> result
                }
            },
            cacheFallback = { cache.getFees(studentId).takeIf { it.isNotEmpty() } },
        )

    override suspend fun getPayments(studentId: String?, status: String?): ApiResult<List<PaymentDto>> {
        val safeStudentId = studentId ?: ""
        return safeApiCallWithCache(
            fallback = "Gagal mengambil data pembayaran",
            block = {
                when (val result = api.getPayments(studentId, status)) {
                    is ApiResult.Success -> {
                        cache.cachePayments(safeStudentId, result.data)
                        result
                    }
                    is ApiResult.Error -> result
                }
            },
            cacheFallback = { cache.getPayments(safeStudentId).takeIf { it.isNotEmpty() } },
        )
    }

    override suspend fun getPaymentDetail(id: String): ApiResult<PaymentDto> =
        safeApiCall("Gagal mengambil detail pembayaran") { api.getPaymentDetail(id) }

    override suspend fun getReceiptUrl(id: String): ApiResult<String> =
        safeApiCall("Gagal membuat kwitansi") { api.getReceiptUrl(id) }

    override suspend fun getSnapToken(studentFeeId: String, paymentMethod: String): ApiResult<SnapTokenResponse> =
        safeApiCall("Gagal mendapatkan token pembayaran") { api.getSnapToken(studentFeeId, paymentMethod) }

    override suspend fun checkPaymentStatus(chargeId: String): ApiResult<PaymentDto> =
        safeApiCall("Gagal mengecek status pembayaran") {
            when (val result = api.checkPaymentStatus(chargeId)) {
                is ApiResult.Success -> {
                    cache.cachePayments("", listOf(result.data))
                    result
                }
                is ApiResult.Error -> result
            }
        }

    override suspend fun getPaymentMethods(): ApiResult<List<PaymentMethodDto>> =
        safeApiCall("Gagal mengambil metode pembayaran") { api.getPaymentMethods() }
}
