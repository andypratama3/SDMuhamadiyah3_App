package com.sdm3.parent.core.navigation

import com.sdm3.parent.domain.model.RoleContext
import com.sdm3.parent.domain.model.UserRole

object PostAuthNavigator {

    fun resolveRoute(
        roleContext: RoleContext,
        selectedStudentId: String?,
        onboardingCompleted: Boolean,
    ): SDM3Route = when {
        roleContext.primary == UserRole.UNKNOWN -> SDM3Route.Login
        roleContext.isTeacherOnly() -> SDM3Route.TeacherHome
        !onboardingCompleted && roleContext.hasParentAccess -> SDM3Route.Onboarding
        roleContext.hasParentAccess && selectedStudentId.isNullOrBlank() -> SDM3Route.PilihAnak
        roleContext.hasParentAccess -> SDM3Route.Main(selectedStudentId.orEmpty())
        roleContext.hasTeacherAccess -> SDM3Route.TeacherHome
        else -> SDM3Route.Login
    }

    fun isPublicRoute(routeStr: String): Boolean =
        routeStr.contains("Splash") ||
            routeStr.contains("Onboarding") ||
            routeStr.contains("Login") ||
            routeStr.contains("VerifikasiOtp")

    fun requiresTeacherCapability(route: SDM3Route): Boolean = when (route) {
        is SDM3Route.TeacherHome,
        is SDM3Route.GuruAbsensi -> true
        else -> false
    }

    fun requiresTeacherCapability(routeStr: String): Boolean =
        routeStr.contains("TeacherHome") || routeStr.contains("GuruAbsensi")

    fun requiresParentCapability(route: SDM3Route): Boolean = when (route) {
        is SDM3Route.Splash,
        is SDM3Route.Onboarding,
        is SDM3Route.Login,
        is SDM3Route.VerifikasiOtp,
        is SDM3Route.TeacherHome,
        is SDM3Route.GuruAbsensi -> false
        else -> true
    }

    fun requiresParentCapability(routeStr: String): Boolean =
        !isPublicRoute(routeStr) && !requiresTeacherCapability(routeStr)
}
