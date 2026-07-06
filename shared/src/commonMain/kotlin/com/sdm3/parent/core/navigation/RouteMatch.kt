package com.sdm3.parent.core.navigation

/** Typed route matching — avoids false positives (e.g. TeacherHome matching "Home"). */

fun isParentMainRoute(routeStr: String): Boolean =
    routeStr.contains("Main") && !routeStr.contains("TeacherHome")

fun isParentBottomNavRoute(routeStr: String): Boolean = when {
    isParentMainRoute(routeStr) -> true
    routeStr.contains("NilaiRapor") -> true
    routeStr.contains("PembayaranSpp") -> true
    routeStr.contains("HalamanRapor") -> true
    routeStr.contains("ProfilAkun") -> true
    else -> false
}

fun parentBottomTabForRoute(routeStr: String): SDM3BottomTab? = when {
    isParentMainRoute(routeStr) -> SDM3BottomTab.Beranda
    routeStr.contains("NilaiRapor") -> SDM3BottomTab.Nilai
    routeStr.contains("PembayaranSpp") -> SDM3BottomTab.Bayar
    routeStr.contains("HalamanRapor") -> SDM3BottomTab.Rapor
    routeStr.contains("ProfilAkun") -> SDM3BottomTab.Profil
    else -> null
}
