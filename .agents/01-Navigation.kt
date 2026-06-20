/*
 * SDM3 Parent Portal — Navigasi Multiplatform (Android + iOS)
 *
 * Kenapa BUKAN androidx.navigation3 (yang ditulis di blueprint asli)?
 * - androidx.navigation3 awalnya Android-only.
 * - Dukungan non-Android baru ada sejak Compose Multiplatform 1.10.0 (awal 2026)
 *   lewat artifact org.jetbrains.androidx.navigation3:navigation3-ui — dan
 *   statusnya MASIH ALPHA per Juni 2026.
 *
 * Untuk app produksi yang harus lolos review Play Store/App Store dengan stabil,
 * kita pakai fork JetBrains yang sudah STABLE dan dipakai banyak app production:
 *
 *   org.jetbrains.androidx.navigation:navigation-compose:2.9.2
 *
 * API-nya 99% identik dengan androidx.navigation Android biasa, dan sudah
 * mendukung type-safe routes via @Serializable — jadi requirement "Navigation
 * harus type-safe" di Section 2 (Feature Agent) tetap terpenuhi.
 */
package com.sdm3.parent.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

/**
 * Seluruh route aplikasi didefinisikan di sini sebagai sealed interface.
 * Setiap destinasi adalah @Serializable data class/object — TIDAK ADA string route
 * yang ditulis manual (mencegah typo route yang baru kelihatan saat runtime).
 */
sealed interface SDM3Route {

    @Serializable
    data object Splash : SDM3Route

    @Serializable
    data object Onboarding : SDM3Route

    @Serializable
    data object Login : SDM3Route

    /** Lupa password dipindah ke flow tersendiri — lihat Phase 9 di blueprint. */
    @Serializable
    data object ForgotPasswordViaSchool : SDM3Route

    @Serializable
    data object PilihAnak : SDM3Route

    @Serializable
    data class Home(val studentId: String) : SDM3Route

    @Serializable
    data class NilaiRapor(val studentId: String, val semester: String) : SDM3Route

    @Serializable
    data class DetailNilaiMapel(
        val studentId: String,
        val subjectId: String,
        val semester: String
    ) : SDM3Route

    @Serializable
    data class PembayaranSpp(val studentId: String) : SDM3Route

    @Serializable
    data class PilihMetodeBayar(val studentFeeId: String) : SDM3Route

    @Serializable
    data class ProsesPembayaran(val paymentId: String) : SDM3Route

    @Serializable
    data class PembayaranBerhasil(val paymentId: String) : SDM3Route

    @Serializable
    data class KehadiranSiswa(val studentId: String) : SDM3Route

    @Serializable
    data object Notifikasi : SDM3Route

    @Serializable
    data object ProfilAkun : SDM3Route

    /**
     * WAJIB ada untuk lolos syarat Play Store & App Store (lihat Bagian 3.1 di
     * ANALISIS-DAN-PERBAIKAN-BLUEPRINT.md). Tidak ada di blueprint v1.0.0 asli.
     */
    @Serializable
    data object AccountDeletion : SDM3Route
}

/**
 * Root NavHost aplikasi. Dipanggil sekali dari MainActivity (Android) /
 * ContentView (iOS) lewat composable bersama di commonMain.
 *
 * @param navController didapat dari `rememberNavController()` (lihat dokumentasi
 *   resmi: kotlinlang.org/docs/multiplatform/compose-navigation-routing.html)
 * @param startDestination ditentukan oleh SplashViewModel: Onboarding / Login / Home
 */
@Composable
fun SDM3NavHost(
    navController: NavHostController,
    startDestination: SDM3Route
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable<SDM3Route.Splash> {
            // SplashScreen(onNavigate = { route -> navController.navigate(route) { popUpTo... } })
        }

        composable<SDM3Route.Login> {
            // LoginScreen(
            //     onLoginSuccess = { navController.navigate(SDM3Route.PilihAnak) },
            //     onForgotPassword = { navController.navigate(SDM3Route.ForgotPasswordViaSchool) }
            // )
        }

        composable<SDM3Route.Home> { backStackEntry ->
            val route = backStackEntry.toRoute<SDM3Route.Home>()
            // HomeScreen(studentId = route.studentId, navController = navController)
        }

        composable<SDM3Route.DetailNilaiMapel> { backStackEntry ->
            val route = backStackEntry.toRoute<SDM3Route.DetailNilaiMapel>()
            // DetailNilaiMapelScreen(
            //     studentId = route.studentId,
            //     subjectId = route.subjectId,
            //     semester = route.semester
            // )
        }

        composable<SDM3Route.ProsesPembayaran> { backStackEntry ->
            val route = backStackEntry.toRoute<SDM3Route.ProsesPembayaran>()
            // ProsesPembayaranScreen(paymentId = route.paymentId)
        }

        composable<SDM3Route.ProfilAkun> {
            // ProfilAkunScreen(
            //     onRequestAccountDeletion = { navController.navigate(SDM3Route.AccountDeletion) }
            // )
        }

        composable<SDM3Route.AccountDeletion> {
            // AccountDeletionScreen() — lihat 04-AccountDeletionFeature.kt
        }

        // ... tambahkan sisa destinasi sesuai Section 5.3 blueprint
    }
}

/*
 * SETUP GRADLE (commonMain dependencies):
 *
 * commonMain.dependencies {
 *     implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.2")
 * }
 *
 * Tidak perlu dependency tambahan untuk Android/iOS — fork ini sudah multiplatform
 * dari satu artifact yang sama.
 */
