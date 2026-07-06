package com.sdm3.parent.domain.model

enum class UserRole {
    PARENT,
    TEACHER,
    ADMIN,
    UNKNOWN;

    fun isTeacher(): Boolean = this == TEACHER || this == ADMIN

    fun isParent(): Boolean = this == PARENT || this == ADMIN

    fun isTeacherOnly(): Boolean = this == TEACHER

    fun isParentOnly(): Boolean = this == PARENT

    companion object {
        fun fromApi(value: String?): UserRole = when (value?.lowercase()) {
            "parent" -> PARENT
            "teacher", "admin_akademik" -> TEACHER
            "admin", "superadmin" -> ADMIN
            else -> UNKNOWN
        }
    }
}
