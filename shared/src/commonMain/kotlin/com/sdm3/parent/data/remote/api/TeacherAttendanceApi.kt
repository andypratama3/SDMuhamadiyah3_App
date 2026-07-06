package com.sdm3.parent.data.remote.api

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.network.toApiResult
import com.sdm3.parent.data.remote.dto.TeacherBulkAttendanceRequest
import com.sdm3.parent.data.remote.dto.TeacherClassroomDto
import com.sdm3.parent.data.remote.dto.TeacherRosterDto
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class TeacherAttendanceApi(private val provider: HttpClientProvider) {

    suspend fun getClassrooms(): ApiResult<List<TeacherClassroomDto>> {
        val response = provider.client.get {
            url(Endpoints.TEACHER_CLASSROOMS)
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun getRoster(classroomId: String, date: String): ApiResult<TeacherRosterDto> {
        val response = provider.client.get {
            url(Endpoints.TEACHER_CLASSROOM_ROSTER.replace("{classroomId}", classroomId))
            parameter("date", date)
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }

    suspend fun bulkMark(request: TeacherBulkAttendanceRequest): ApiResult<Unit> {
        val response = provider.client.post {
            url(Endpoints.TEACHER_ATTENDANCES_BULK)
            provider.applyAuthHeader(this)
            setBody(request)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }
}
