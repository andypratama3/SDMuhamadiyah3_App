package com.sdm3.parent.domain.entity

data class Attendance(
    val id: String,
    val date: String,
    val status: AttendanceStatus,
    val note: String?
)

enum class AttendanceStatus {
    HADIR, SAKIT, IZIN, ALPA, PULANG,
    PRESENT, ABSENT, LATE, EXCUSED
}

data class AttendanceSummary(
    val hadir: Int,
    val sakit: Int,
    val izin: Int,
    val alpa: Int
)
