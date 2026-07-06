package com.sdm3.parent.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.sdm3.parent.core.security.SecureTokenManager
import com.sdm3.parent.domain.model.RoleContext

@Composable
fun RouteAccessGate(
    currentRouteStr: String,
    secureTokenManager: SecureTokenManager,
    navController: NavHostController,
    content: @Composable () -> Unit,
) {
    val roleContext = secureTokenManager.getRoleContext()
    val redirect = resolveUnauthorizedRedirect(currentRouteStr, roleContext)

    LaunchedEffect(currentRouteStr, roleContext, redirect) {
        if (redirect == null) return@LaunchedEffect
        if (currentRouteStr.isBlank()) return@LaunchedEffect
        if (navController.currentBackStackEntry == null) return@LaunchedEffect
        navController.navigate(redirect) {
            popUpTo(0) { inclusive = true }
        }
    }

    content()
}

fun resolveUnauthorizedRedirect(
    routeStr: String,
    roleContext: RoleContext,
): SDM3Route? {
    if (routeStr.isBlank() || PostAuthNavigator.isPublicRoute(routeStr)) return null

    if (PostAuthNavigator.requiresTeacherCapability(routeStr) && !roleContext.hasTeacherAccess) {
        return if (roleContext.hasParentAccess) {
            SDM3Route.PilihAnak
        } else {
            SDM3Route.Login
        }
    }

    if (PostAuthNavigator.requiresParentCapability(routeStr) && !roleContext.hasParentAccess) {
        return if (roleContext.hasTeacherAccess) {
            SDM3Route.TeacherHome
        } else {
            SDM3Route.Login
        }
    }

    return null
}

fun isDeepLinkAllowed(route: SDM3Route, roleContext: RoleContext): Boolean =
    when {
        PostAuthNavigator.requiresTeacherCapability(route) -> roleContext.hasTeacherAccess
        PostAuthNavigator.requiresParentCapability(route) -> roleContext.hasParentAccess
        else -> true
    }
