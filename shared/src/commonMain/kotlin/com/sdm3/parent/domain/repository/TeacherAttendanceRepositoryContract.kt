package com.sdm3.parent.domain.repository

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.TeacherClassroomDto
import com.sdm3.parent.data.remote.dto.TeacherRosterDto

interface TeacherAttendanceRepositoryContract {
    suspend fun getClassrooms(): ApiResult<List<TeacherClassroomDto>>
    suspend fun getRoster(classroomId: String, date: String): ApiResult<TeacherRosterDto>
    suspend fun saveAttendance(classroomId: String, date: String, records: Map<String, String>): ApiResult<Unit>
}
