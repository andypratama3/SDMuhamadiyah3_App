package com.sdm3.parent.data.remote.api

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.network.toApiResult
import com.sdm3.parent.data.remote.dto.GradeComponentDto
import com.sdm3.parent.data.remote.dto.GradeDto
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

class GradeApi(private val provider: HttpClientProvider) {

    suspend fun getGrades(studentId: String, semester: String? = null): ApiResult<List<GradeDto>> {
        val response = provider.client.get {
            url(Endpoints.PARENT_GRADES)
            provider.applyAuthHeader(this)
            parameter("student_id", studentId)
            semester?.let { parameter("semester", it) }
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun getGradeComponents(studentId: String, subjectId: String): ApiResult<List<GradeComponentDto>> {
        val response = provider.client.get {
            url(Endpoints.PARENT_GRADE_COMPONENTS)
            provider.applyAuthHeader(this)
            parameter("student_id", studentId)
            parameter("subject_id", subjectId)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }
}
