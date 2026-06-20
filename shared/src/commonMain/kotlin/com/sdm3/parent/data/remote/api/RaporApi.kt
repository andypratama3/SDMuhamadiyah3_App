package com.sdm3.parent.data.remote.api

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.network.toApiResult
import com.sdm3.parent.data.remote.dto.RaporInstanceDto
import com.sdm3.parent.data.remote.dto.RaporVerifyResponse
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import kotlinx.serialization.Serializable

class RaporApi(private val provider: HttpClientProvider) {

    suspend fun getRaporInstances(studentId: String): ApiResult<List<RaporInstanceDto>> {
        val response = provider.client.get {
            url(Endpoints.PARENT_RAPOR_INSTANCES)
            provider.applyAuthHeader(this)
            parameter("student_id", studentId)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun getDownloadUrl(id: String): ApiResult<String> {
        val response = provider.client.get {
            url(Endpoints.PARENT_RAPOR_DOWNLOAD.replace("{id}", id))
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun verifyQr(qrData: String): ApiResult<RaporVerifyResponse> {
        val response = provider.client.post {
            url(Endpoints.PARENT_RAPOR_VERIFY)
            provider.applyAuthHeader(this)
            setBody(RaporVerifyRequest(qrData = qrData))
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    @Serializable
    private data class RaporVerifyRequest(val qrData: String)
}
