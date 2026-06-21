package com.sdm3.parent.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sdm3.parent.core.designsystem.component.SDM3BottomNavBar
import com.sdm3.parent.feature.auth.ui.AccountDeletionScreen
import com.sdm3.parent.feature.auth.ui.LoginScreen
import com.sdm3.parent.feature.auth.ui.OnboardingScreen
import com.sdm3.parent.feature.auth.ui.PilihAnakScreen
import com.sdm3.parent.feature.auth.ui.SplashScreen
import com.sdm3.parent.feature.auth.ui.VerifikasiOtpScreen
import com.sdm3.parent.feature.auth.VerifikasiOtpViewModel
import org.koin.compose.viewmodel.koinViewModel
import com.sdm3.parent.feature.home.HomeScreen
import com.sdm3.parent.feature.kehadiran.ui.KehadiranSiswaScreen
import com.sdm3.parent.feature.nilai.NilaiRaporScreen
import com.sdm3.parent.feature.nilai.ui.DetailNilaiMapelScreen
import com.sdm3.parent.feature.notifikasi.ui.NotifikasiScreen
import com.sdm3.parent.feature.notifikasi.ui.PengumumanSekolahScreen
import com.sdm3.parent.feature.pembayaran.ui.DetailBuktiBayarScreen
import com.sdm3.parent.feature.pembayaran.ui.PembayaranBerhasilScreen
import com.sdm3.parent.feature.pembayaran.ui.PembayaranSppScreen
import com.sdm3.parent.feature.pembayaran.ui.PilihMetodeBayarScreen
import com.sdm3.parent.feature.pembayaran.ui.ProsesPembayaranScreen
import com.sdm3.parent.feature.profil.ui.PengaturanNotifikasiScreen
import com.sdm3.parent.feature.profil.ui.ProfilAkunScreen
import com.sdm3.parent.feature.infoanak.ui.DetailInfoAnakScreen
import com.sdm3.parent.feature.infoanak.ui.KegiatanProgramScreen
import com.sdm3.parent.feature.nilai.ui.HalamanRaporScreen
import com.sdm3.parent.core.security.SecureTokenManager
import com.sdm3.parent.feature.rapor.PreviewRaporPdfScreen
import com.sdm3.parent.feature.rapor.VerifikasiQrRaporScreen

@Composable
fun SDM3NavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: SDM3Route = SDM3Route.Splash,
    secureTokenManager: SecureTokenManager = org.koin.compose.koinInject()
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRouteStr = navBackStackEntry?.destination?.route ?: ""

    val bottomTabs = listOf("Home", "NilaiRapor", "PembayaranSpp", "HalamanRapor", "ProfilAkun", "Main")
    val showBottomBar = bottomTabs.any { currentRouteStr.contains(it) }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                val currentTab = when {
                    currentRouteStr.contains("Home") || currentRouteStr.contains("Main") -> SDM3BottomTab.Beranda
                    currentRouteStr.contains("NilaiRapor") -> SDM3BottomTab.Nilai
                    currentRouteStr.contains("PembayaranSpp") -> SDM3BottomTab.Bayar
                    currentRouteStr.contains("HalamanRapor") -> SDM3BottomTab.Rapor
                    currentRouteStr.contains("ProfilAkun") -> SDM3BottomTab.Profil
                    else -> null
                }
                SDM3BottomNavBar(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        val studentId = secureTokenManager.getSelectedStudentId() ?: ""
                        val route: SDM3Route = when (tab) {
                            SDM3BottomTab.Beranda -> SDM3Route.Home(studentId)
                            SDM3BottomTab.Nilai -> SDM3Route.NilaiRapor(studentId, "ganjil")
                            SDM3BottomTab.Bayar -> SDM3Route.PembayaranSpp(studentId)
                            SDM3BottomTab.Rapor -> SDM3Route.HalamanRapor(studentId)
                            SDM3BottomTab.Profil -> SDM3Route.ProfilAkun
                        }
                        navController.navigate(route) {
                            popUpTo<SDM3Route.Main> { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {

            composable<SDM3Route.Splash> {
                SplashScreen(onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo<SDM3Route.Splash> { inclusive = true }
                    }
                })
            }

            composable<SDM3Route.Onboarding> {
                OnboardingScreen(onComplete = {
                    navController.navigate(SDM3Route.Login) {
                        popUpTo<SDM3Route.Onboarding> { inclusive = true }
                    }
                })
            }

            composable<SDM3Route.Login> {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(SDM3Route.PilihAnak) {
                            popUpTo<SDM3Route.Login> { inclusive = true }
                        }
                    },
                    onForgotPassword = { email ->
                        navController.navigate(SDM3Route.VerifikasiOtp(email))
                    }
                )
            }

            composable<SDM3Route.VerifikasiOtp> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.VerifikasiOtp>()
                val viewModel: VerifikasiOtpViewModel = koinViewModel()
                viewModel.setEmail(route.email)
                VerifikasiOtpScreen(
                    viewModel = viewModel,
                    onSuccess = {
                        navController.navigate(SDM3Route.Login) {
                            popUpTo<SDM3Route.VerifikasiOtp> { inclusive = true }
                        }
                    }
                )
            }

            composable<SDM3Route.PilihAnak> {
                PilihAnakScreen(
                    onChildSelected = { studentId ->
                        navController.navigate(SDM3Route.Main(studentId)) {
                            popUpTo<SDM3Route.PilihAnak> { inclusive = true }
                        }
                    }
                )
            }

            composable<SDM3Route.Main> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.Main>()
                MainScreen(
                    studentId = route.studentId,
                    navController = navController
                )
            }

            composable<SDM3Route.Home> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.Home>()
                HomeScreen(
                    studentId = route.studentId,
                    navController = navController
                )
            }

            composable<SDM3Route.NilaiRapor> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.NilaiRapor>()
                NilaiRaporScreen(
                    studentId = route.studentId,
                    semester = route.semester,
                    onBack = { navController.popBackStack() },
                    onDetailMapel = { subjectId ->
                        navController.navigate(SDM3Route.DetailNilaiMapel(route.studentId, subjectId, route.semester))
                    }
                )
            }

            composable<SDM3Route.DetailNilaiMapel> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.DetailNilaiMapel>()
                DetailNilaiMapelScreen(
                    studentId = route.studentId,
                    subjectId = route.subjectId,
                    semester = route.semester,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<SDM3Route.PembayaranSpp> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.PembayaranSpp>()
                PembayaranSppScreen(
                    studentId = route.studentId,
                    onBack = { navController.popBackStack() },
                    onBayarSekarang = { feeId ->
                        navController.navigate(SDM3Route.PilihMetodeBayar(feeId))
                    },
                    onDetailBukti = { paymentId ->
                        navController.navigate(SDM3Route.DetailBuktiBayar(paymentId))
                    }
                )
            }

            composable<SDM3Route.PilihMetodeBayar> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.PilihMetodeBayar>()
                PilihMetodeBayarScreen(
                    studentFeeId = route.studentFeeId,
                    onBack = { navController.popBackStack() },
                    onLanjutkan = { paymentId ->
                        navController.navigate(SDM3Route.ProsesPembayaran(paymentId))
                    }
                )
            }

            composable<SDM3Route.ProsesPembayaran> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.ProsesPembayaran>()
                ProsesPembayaranScreen(
                    paymentId = route.paymentId,
                    onBack = { navController.popBackStack() },
                    onPembayaranBerhasil = {
                        navController.navigate(SDM3Route.PembayaranBerhasil(route.paymentId)) {
                            popUpTo<SDM3Route.ProsesPembayaran> { inclusive = true }
                        }
                    }
                )
            }

            composable<SDM3Route.PembayaranBerhasil> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.PembayaranBerhasil>()
                PembayaranBerhasilScreen(
                    paymentId = route.paymentId,
                    onLihatBukti = {
                        navController.navigate(SDM3Route.DetailBuktiBayar(route.paymentId))
                    },
                    onKembali = {
                        navController.popBackStack()
                    }
                )
            }

            composable<SDM3Route.DetailBuktiBayar> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.DetailBuktiBayar>()
                DetailBuktiBayarScreen(
                    paymentId = route.paymentId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<SDM3Route.KehadiranSiswa> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.KehadiranSiswa>()
                KehadiranSiswaScreen(
                    studentId = route.studentId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<SDM3Route.HalamanRapor> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.HalamanRapor>()
                HalamanRaporScreen(
                    studentId = route.studentId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<SDM3Route.DetailInfoAnak> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.DetailInfoAnak>()
                DetailInfoAnakScreen(
                    studentId = route.studentId,
                    onBack = { navController.popBackStack() },
                    onNavigateToNilai = {
                        navController.navigate(SDM3Route.NilaiRapor(route.studentId, "ganjil"))
                    },
                    onNavigateToRapor = {
                        navController.navigate(SDM3Route.HalamanRapor(route.studentId))
                    },
                    onNavigateToKehadiran = {
                        navController.navigate(SDM3Route.KehadiranSiswa(route.studentId))
                    },
                    onNavigateToKegiatan = {
                        navController.navigate(SDM3Route.KegiatanProgram(route.studentId))
                    }
                )
            }

            composable<SDM3Route.KegiatanProgram> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.KegiatanProgram>()
                KegiatanProgramScreen(
                    studentId = route.studentId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<SDM3Route.Notifikasi> {
                NotifikasiScreen(onBack = { navController.popBackStack() })
            }

            composable<SDM3Route.PengumumanSekolah> {
                PengumumanSekolahScreen(onBack = { navController.popBackStack() })
            }

            composable<SDM3Route.ProfilAkun> {
                ProfilAkunScreen(
                    onNotifikasiSetting = {
                        navController.navigate(SDM3Route.PengaturanNotifikasi)
                    },
                    onAccountDeletion = {
                        navController.navigate(SDM3Route.AccountDeletion)
                    },
                    onLogout = {
                        navController.navigate(SDM3Route.Login) {
                            popUpTo<SDM3Route.Main> { inclusive = true }
                        }
                    }
                )
            }

            composable<SDM3Route.PengaturanNotifikasi> {
                PengaturanNotifikasiScreen(onBack = { navController.popBackStack() })
            }

            composable<SDM3Route.AccountDeletion> {
                AccountDeletionScreen(
                    onBack = { navController.popBackStack() },
                    onDeletionRequestSubmitted = {
                        navController.navigate(SDM3Route.Login) {
                            popUpTo<SDM3Route.AccountDeletion> { inclusive = true }
                        }
                    }
                )
            }

            composable<SDM3Route.PreviewRaporPdf> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.PreviewRaporPdf>()
                PreviewRaporPdfScreen(
                    raporId = route.raporId,
                    downloadUrl = route.downloadUrl,
                    viewModel = org.koin.compose.koinInject(),
                    onBack = { navController.popBackStack() }
                )
            }

            composable<SDM3Route.VerifikasiQrRapor> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.VerifikasiQrRapor>()
                VerifikasiQrRaporScreen(
                    raporId = route.raporId,
                    viewModel = org.koin.compose.koinInject(),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun MainScreen(
    studentId: String,
    navController: NavHostController
) {
    HomeScreen(
        studentId = studentId,
        navController = navController
    )
}
