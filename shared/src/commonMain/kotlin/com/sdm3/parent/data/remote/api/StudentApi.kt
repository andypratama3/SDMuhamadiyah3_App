package com.sdm3.parent.data.remote.api

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.network.toApiResult
import com.sdm3.parent.data.remote.dto.StudentDto
import io.ktor.client.request.get
import io.ktor.client.request.url

class StudentApi(private val provider: HttpClientProvider) {

    suspend fun getStudents(): ApiResult<List<StudentDto>> {
        val response = provider.client.get {
            url(Endpoints.PARENT_STUDENTS)
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun getStudentDetail(id: String): ApiResult<StudentDto> {
        val response = provider.client.get {
            url(Endpoints.PARENT_STUDENT_DETAIL.replace("{id}", id))
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }
}
