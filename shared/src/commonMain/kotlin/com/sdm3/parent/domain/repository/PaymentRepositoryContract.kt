package com.sdm3.parent.domain.repository

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.PaymentDto
import com.sdm3.parent.data.remote.dto.PaymentMethodDto
import com.sdm3.parent.data.remote.dto.SnapTokenResponse
import com.sdm3.parent.data.remote.dto.StudentFeeDto

interface PaymentRepositoryContract {
    suspend fun getStudentFees(studentId: String): ApiResult<List<StudentFeeDto>>
    suspend fun getPayments(studentId: String? = null, status: String? = null): ApiResult<List<PaymentDto>>
    suspend fun getPaymentDetail(id: String): ApiResult<PaymentDto>
    suspend fun getSnapToken(studentFeeId: String, paymentMethod: String): ApiResult<SnapTokenResponse>
    suspend fun checkPaymentStatus(chargeId: String): ApiResult<PaymentDto>
    suspend fun getPaymentMethods(): ApiResult<List<PaymentMethodDto>>
}
