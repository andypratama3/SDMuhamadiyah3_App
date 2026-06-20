package com.sdm3.parent.data.remote.api

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.network.toApiResult
import com.sdm3.parent.data.remote.dto.PaymentDto
import com.sdm3.parent.data.remote.dto.SnapTokenResponse
import com.sdm3.parent.data.remote.dto.StudentFeeDto
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

class PaymentApi(private val provider: HttpClientProvider) {

    suspend fun getStudentFees(studentId: String): ApiResult<List<StudentFeeDto>> {
        val response = provider.client.get {
            url(Endpoints.PARENT_STUDENT_FEES)
            provider.applyAuthHeader(this)
            parameter("student_id", studentId)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun getPayments(studentId: String, status: String? = null): ApiResult<List<PaymentDto>> {
        val response = provider.client.get {
            url(Endpoints.PARENT_PAYMENTS)
            provider.applyAuthHeader(this)
            parameter("student_id", studentId)
            status?.let { parameter("status", it) }
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun getSnapToken(paymentId: String): ApiResult<SnapTokenResponse> {
        val response = provider.client.get {
            url(Endpoints.MIDTRANS_SNAP_TOKEN.replace("{payment}", paymentId))
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun checkPaymentStatus(chargeId: String): ApiResult<PaymentDto> {
        val response = provider.client.get {
            url(Endpoints.MIDTRANS_STATUS.replace("{chargeId}", chargeId))
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }
}
