package com.sdm3.parent.data.remote.api

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.network.toApiResult
import com.sdm3.parent.data.remote.dto.PaymentDto
import com.sdm3.parent.data.remote.dto.PaymentMethodDto
import com.sdm3.parent.data.remote.dto.SnapTokenRequest
import com.sdm3.parent.data.remote.dto.SnapTokenResponse
import com.sdm3.parent.data.remote.dto.StudentFeeDto
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
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

    suspend fun getPayments(studentId: String? = null, status: String? = null): ApiResult<List<PaymentDto>> {
        val response = provider.client.get {
            url(Endpoints.PARENT_PAYMENTS)
            provider.applyAuthHeader(this)
            studentId?.let { parameter("student_id", it) }
            status?.let { parameter("status", it) }
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun getPaymentDetail(id: String): ApiResult<PaymentDto> {
        val response = provider.client.get {
            url(Endpoints.PARENT_PAYMENT_DETAIL.replace("{id}", id))
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun getSnapToken(studentFeeId: String, paymentMethod: String): ApiResult<SnapTokenResponse> {
        val response = provider.client.post {
            url(Endpoints.MIDTRANS_SNAP_TOKEN.replace("{payment}", studentFeeId))
            provider.applyAuthHeader(this)
            setBody(SnapTokenRequest(paymentMethod = paymentMethod))
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

    suspend fun getPaymentMethods(): ApiResult<List<PaymentMethodDto>> {
        val response = provider.client.get {
            url(Endpoints.MIDTRANS_PAYMENT_METHODS)
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }
}
