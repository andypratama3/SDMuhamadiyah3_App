package com.sdm3.parent.domain.entity

data class Student(
    val id: String,
    val userId: String?,
    val nisn: String,
    val nis: String?,
    val name: String,
    val className: String,
    val classroomId: String,
    val academicYear: String,
    val gender: Gender,
    val birthPlace: String,
    val birthDate: String,
    val photoUrl: String?,
    val status: StudentStatus,
    val fatherName: String?,
    val motherName: String?,
    val parentPhone: String?,
    val isInklusi: Boolean = false
)

enum class Gender { LakiLaki, Perempuan }
enum class StudentStatus { ACTIVE, INACTIVE, GRADUATED, TRANSFER_OUT, DROPPED }
