package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.PaymentApi
import com.sdm3.parent.data.remote.dto.PaymentDto
import com.sdm3.parent.data.remote.dto.SnapTokenResponse
import com.sdm3.parent.data.remote.dto.StudentFeeDto

class PaymentRepository(private val api: PaymentApi) {

    suspend fun getStudentFees(studentId: String): ApiResult<List<StudentFeeDto>> {
        return try {
            api.getStudentFees(studentId)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data tagihan"))
        }
    }

    suspend fun getPayments(studentId: String, status: String? = null): ApiResult<List<PaymentDto>> {
        return try {
            api.getPayments(studentId, status)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data pembayaran"))
        }
    }

    suspend fun getSnapToken(paymentId: String): ApiResult<SnapTokenResponse> {
        return try {
            api.getSnapToken(paymentId)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mendapatkan token pembayaran"))
        }
    }

    suspend fun checkPaymentStatus(chargeId: String): ApiResult<PaymentDto> {
        return try {
            api.checkPaymentStatus(chargeId)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengecek status pembayaran"))
        }
    }
}
