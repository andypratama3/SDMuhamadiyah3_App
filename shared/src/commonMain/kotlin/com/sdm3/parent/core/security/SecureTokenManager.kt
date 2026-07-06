package com.sdm3.parent.core.security

import com.sdm3.parent.domain.model.RoleContext
import com.sdm3.parent.domain.model.UserRole

private const val KEY_BEARER_TOKEN = "sdm3_bearer_token"
private const val KEY_SELECTED_STUDENT_ID = "sdm3_selected_student_id"
private const val KEY_USER_ID = "sdm3_user_id"
private const val KEY_USER_ROLE = "sdm3_user_role"
private const val KEY_HAS_PARENT_ACCESS = "sdm3_has_parent_access"
private const val KEY_HAS_TEACHER_ACCESS = "sdm3_has_teacher_access"
private const val KEY_BIOMETRIC_ENABLED = "sdm3_biometric_enabled"
private const val KEY_FCM_TOKEN = "sdm3_fcm_token"
private const val KEY_ONBOARDING_COMPLETED = "sdm3_onboarding_completed"
private const val KEY_LAST_NILAI_SEMESTER = "sdm3_last_nilai_semester"

class SecureTokenManager(private val storage: SecureStorage) {

    fun saveBearerToken(token: String) {
        storage.set(key = KEY_BEARER_TOKEN, value = token)
    }

    fun getBearerToken(): String? = storage.string(forKey = KEY_BEARER_TOKEN)

    fun saveSelectedStudentId(studentId: String) {
        storage.set(key = KEY_SELECTED_STUDENT_ID, value = studentId)
    }

    fun getSelectedStudentId(): String? = storage.string(forKey = KEY_SELECTED_STUDENT_ID)

    fun saveUserId(userId: String) {
        storage.set(key = KEY_USER_ID, value = userId)
    }

    fun getUserId(): String? = storage.string(forKey = KEY_USER_ID)

    fun saveUserRole(role: UserRole) {
        storage.set(key = KEY_USER_ROLE, value = role.name)
    }

    fun saveRoleContext(context: RoleContext) {
        saveUserRole(context.primary)
        storage.set(key = KEY_HAS_PARENT_ACCESS, value = context.hasParentAccess)
        storage.set(key = KEY_HAS_TEACHER_ACCESS, value = context.hasTeacherAccess)
    }

    fun getRoleContext(): RoleContext {
        val primary = storage.string(forKey = KEY_USER_ROLE)
            ?.let { runCatching { UserRole.valueOf(it) }.getOrNull() }
            ?: UserRole.UNKNOWN
        val hasParent = storage.bool(forKey = KEY_HAS_PARENT_ACCESS)
        val hasTeacher = storage.bool(forKey = KEY_HAS_TEACHER_ACCESS)
        return if (hasParent == null && hasTeacher == null) {
            RoleContext(
                primary = primary,
                hasParentAccess = primary.isParent(),
                hasTeacherAccess = primary.isTeacher(),
            )
        } else {
            RoleContext(
                primary = primary,
                hasParentAccess = hasParent ?: false,
                hasTeacherAccess = hasTeacher ?: false,
            )
        }
    }

    fun getUserRole(): UserRole = getRoleContext().primary

    fun setBiometricEnabled(enabled: Boolean) {
        storage.set(key = KEY_BIOMETRIC_ENABLED, value = enabled)
    }

    fun isBiometricEnabled(): Boolean = storage.bool(forKey = KEY_BIOMETRIC_ENABLED) ?: false

    fun setOnboardingCompleted(completed: Boolean) {
        storage.set(key = KEY_ONBOARDING_COMPLETED, value = completed)
    }

    fun isOnboardingCompleted(): Boolean = storage.bool(forKey = KEY_ONBOARDING_COMPLETED) ?: false

    fun saveFcmToken(token: String) {
        storage.set(key = KEY_FCM_TOKEN, value = token)
    }

    fun getFcmToken(): String? = storage.string(forKey = KEY_FCM_TOKEN)

    fun saveLastNilaiSemester(semester: String) {
        if (semester.isNotBlank()) {
            storage.set(key = KEY_LAST_NILAI_SEMESTER, value = semester)
        }
    }

    fun getLastNilaiSemester(): String? = storage.string(forKey = KEY_LAST_NILAI_SEMESTER)

    fun clearFcmToken() {
        storage.deleteObject(forKey = KEY_FCM_TOKEN)
    }

    fun clearAllSecureData() {
        storage.deleteObject(forKey = KEY_BEARER_TOKEN)
        storage.deleteObject(forKey = KEY_SELECTED_STUDENT_ID)
        storage.deleteObject(forKey = KEY_USER_ID)
        storage.deleteObject(forKey = KEY_USER_ROLE)
        storage.deleteObject(forKey = KEY_HAS_PARENT_ACCESS)
        storage.deleteObject(forKey = KEY_HAS_TEACHER_ACCESS)
        storage.deleteObject(forKey = KEY_FCM_TOKEN)
        storage.deleteObject(forKey = KEY_BIOMETRIC_ENABLED)
        storage.deleteObject(forKey = KEY_LAST_NILAI_SEMESTER)
    }

    /**
     * Dipakai saat terdeteksi (re)install baru: bersihkan seluruh data termasuk
     * flag onboarding, karena di iOS Keychain bertahan setelah uninstall sehingga
     * token/onboarding lama bisa "nyangkut" dan membuat onboarding tidak muncul.
     */
    fun resetForFreshInstall() {
        clearAllSecureData()
        storage.deleteObject(forKey = KEY_ONBOARDING_COMPLETED)
    }
}
