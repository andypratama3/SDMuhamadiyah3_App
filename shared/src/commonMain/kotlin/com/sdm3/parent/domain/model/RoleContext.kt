package com.sdm3.parent.domain.model

import com.sdm3.parent.data.remote.dto.ProfileDto
import com.sdm3.parent.data.remote.dto.UserDto

data class RoleContext(
    val primary: UserRole,
    val hasParentAccess: Boolean,
    val hasTeacherAccess: Boolean,
) {
    fun isTeacherOnly(): Boolean = hasTeacherAccess && !hasParentAccess

    fun isParentOnly(): Boolean = hasParentAccess && !hasTeacherAccess

    companion object {
        private val parentRoles = setOf("parent", "admin", "superadmin")
        private val teacherRoles = setOf("teacher", "admin_akademik", "admin", "superadmin")

        fun fromUser(dto: UserDto): RoleContext = fromRoles(dto.role, dto.roles)

        fun fromProfile(dto: ProfileDto): RoleContext = fromRoles(dto.role, dto.roles)

        private fun fromRoles(primaryRole: String?, roleList: List<String>): RoleContext {
            val roles = buildSet {
                primaryRole?.lowercase()?.let { add(it) }
                roleList.forEach { add(it.lowercase()) }
            }
            val hasParent = roles.any { it in parentRoles }
            val hasTeacher = roles.any { it in teacherRoles }
            val primary = when {
                roles.isEmpty() || (!hasParent && !hasTeacher) -> UserRole.UNKNOWN
                hasTeacher && !hasParent -> UserRole.TEACHER
                hasParent && !hasTeacher -> UserRole.PARENT
                hasParent && hasTeacher -> UserRole.PARENT
                roles.contains("admin") || roles.contains("superadmin") -> UserRole.ADMIN
                hasTeacher -> UserRole.TEACHER
                else -> UserRole.PARENT
            }
            return RoleContext(primary, hasParent, hasTeacher)
        }
    }
}
