package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.safeApiCall
import com.sdm3.parent.data.remote.api.TeacherAttendanceApi
import com.sdm3.parent.data.remote.dto.TeacherAttendanceRecordDto
import com.sdm3.parent.data.remote.dto.TeacherBulkAttendanceRequest
import com.sdm3.parent.data.remote.dto.TeacherClassroomDto
import com.sdm3.parent.data.remote.dto.TeacherRosterDto
import com.sdm3.parent.domain.repository.TeacherAttendanceRepositoryContract

class TeacherAttendanceRepository(
    private val api: TeacherAttendanceApi,
) : TeacherAttendanceRepositoryContract {

    override suspend fun getClassrooms(): ApiResult<List<TeacherClassroomDto>> =
        safeApiCall("Gagal mengambil data kelas") { api.getClassrooms() }

    override suspend fun getRoster(classroomId: String, date: String): ApiResult<TeacherRosterDto> =
        safeApiCall("Gagal mengambil daftar siswa") { api.getRoster(classroomId, date) }

    override suspend fun saveAttendance(
        classroomId: String,
        date: String,
        records: Map<String, String>,
    ): ApiResult<Unit> = safeApiCall("Gagal menyimpan absensi") {
        api.bulkMark(
            TeacherBulkAttendanceRequest(
                classroomId = classroomId,
                date = date,
                records = records.map { (studentId, status) ->
                    TeacherAttendanceRecordDto(studentId = studentId, status = status)
                },
            ),
        )
    }
}
