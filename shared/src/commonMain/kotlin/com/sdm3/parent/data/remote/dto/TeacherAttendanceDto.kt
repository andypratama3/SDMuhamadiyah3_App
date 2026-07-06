package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TeacherClassroomDto(
    val id: String,
    val name: String,
    @kotlinx.serialization.SerialName("classroom_type")
    val classroomType: String? = null,
    @kotlinx.serialization.SerialName("academic_year_id")
    val academicYearId: String? = null,
    @kotlinx.serialization.SerialName("student_count")
    val studentCount: Int = 0,
)

@Serializable
data class TeacherRosterStudentDto(
    @kotlinx.serialization.SerialName("student_id")
    val studentId: String,
    val name: String,
    val nis: String? = null,
    val status: String? = null,
    @kotlinx.serialization.SerialName("attendance_id")
    val attendanceId: String? = null,
)

@Serializable
data class TeacherRosterDto(
    @kotlinx.serialization.SerialName("classroom_id")
    val classroomId: String,
    @kotlinx.serialization.SerialName("classroom_name")
    val classroomName: String,
    val date: String,
    val students: List<TeacherRosterStudentDto> = emptyList(),
)

@Serializable
data class TeacherAttendanceRecordDto(
    @kotlinx.serialization.SerialName("student_id")
    val studentId: String,
    val status: String,
)

@Serializable
data class TeacherBulkAttendanceRequest(
    @kotlinx.serialization.SerialName("classroom_id")
    val classroomId: String,
    val date: String,
    val records: List<TeacherAttendanceRecordDto>,
)
