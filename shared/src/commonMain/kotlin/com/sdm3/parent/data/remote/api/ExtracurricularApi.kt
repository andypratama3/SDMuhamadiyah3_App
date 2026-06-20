package com.sdm3.parent.data.remote.api

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.network.toApiResult
import com.sdm3.parent.data.remote.dto.ExtracurricularDto
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

class ExtracurricularApi(private val provider: HttpClientProvider) {

    suspend fun getExtracurriculars(studentId: String): ApiResult<List<ExtracurricularDto>> {
        val response = provider.client.get {
            url(Endpoints.PARENT_EXTRACURRICULARS)
            provider.applyAuthHeader(this)
            parameter("student_id", studentId)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }
}
