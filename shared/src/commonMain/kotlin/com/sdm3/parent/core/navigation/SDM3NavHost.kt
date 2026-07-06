package com.sdm3.parent.core.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sdm3.parent.core.event.SessionEventBus
import com.sdm3.parent.core.notification.PushDeepLinkHolder
import com.sdm3.parent.core.notification.PushDeepLinkNavigator
import com.sdm3.parent.core.designsystem.component.Sdm3AdaptiveLayout
import com.sdm3.parent.feature.auth.ui.AccountDeletionScreen
import com.sdm3.parent.feature.auth.LoginViewModel
import com.sdm3.parent.feature.auth.ui.LoginScreen
import com.sdm3.parent.feature.auth.ui.OnboardingScreen
import com.sdm3.parent.feature.auth.ui.PilihAnakScreen
import com.sdm3.parent.feature.auth.ui.SplashScreen
import com.sdm3.parent.feature.auth.ui.VerifikasiOtpScreen
import com.sdm3.parent.feature.auth.VerifikasiOtpViewModel
import org.koin.compose.viewmodel.koinViewModel
import com.sdm3.parent.feature.home.ui.HomeScreen
import com.sdm3.parent.feature.kehadiran.ui.KehadiranSiswaScreen
import com.sdm3.parent.feature.nilai.ui.NilaiRaporScreen
import com.sdm3.parent.feature.nilai.ui.DetailNilaiMapelScreen
import com.sdm3.parent.feature.notifikasi.ui.DetailPengumumanScreen
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
import com.sdm3.parent.feature.rapor.ui.HalamanRaporScreen
import com.sdm3.parent.feature.rapor.ui.PreviewRaporPdfScreen
import com.sdm3.parent.feature.rapor.ui.VerifikasiQrRaporScreen

private val PremiumEasing = androidx.compose.animation.core.CubicBezierEasing(0.32f, 0.72f, 0f, 1f)

@Composable
fun SDM3NavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: SDM3Route = SDM3Route.Splash
) {

    LaunchedEffect(Unit) {
        SessionEventBus.events.collect {
            navController.navigate(SDM3Route.Login) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRouteStr = navBackStackEntry?.destination?.route ?: ""
    val secureTokenManager: com.sdm3.parent.core.security.SecureTokenManager = org.koin.compose.koinInject()

    val pendingDeepLink by PushDeepLinkHolder.pending.collectAsState()
    LaunchedEffect(pendingDeepLink, currentRouteStr) {
        val link = pendingDeepLink ?: return@LaunchedEffect
        if (!PushDeepLinkNavigator.isAuthenticatedDestination(currentRouteStr)) return@LaunchedEffect
        val route = PushDeepLinkNavigator.toRoute(link, secureTokenManager) ?: return@LaunchedEffect
        PushDeepLinkHolder.consume()
        navController.navigate(route) {
            popUpTo<SDM3Route.Main> {
                saveState = true
            }
            launchSingleTop = true
        }
    }

    val showBottomBar = when {
        currentRouteStr.contains("Main") -> true
        currentRouteStr.contains("Home") -> true
        currentRouteStr.contains("NilaiRapor") -> true
        currentRouteStr.contains("PembayaranSpp") -> true
        currentRouteStr.contains("HalamanRapor") -> true
        currentRouteStr.contains("ProfilAkun") -> true
        else -> false
    }

    val currentTab = when {
        currentRouteStr.contains("Home") || currentRouteStr.contains("Main") -> SDM3BottomTab.Beranda
        currentRouteStr.contains("NilaiRapor") -> SDM3BottomTab.Nilai
        currentRouteStr.contains("PembayaranSpp") -> SDM3BottomTab.Bayar
        currentRouteStr.contains("HalamanRapor") -> SDM3BottomTab.Rapor
        currentRouteStr.contains("ProfilAkun") -> SDM3BottomTab.Profil
        else -> null
    }
    val savedStudentId = secureTokenManager.getSelectedStudentId() ?: ""
    val argStudentId = try {
        when {
            currentRouteStr.contains("Home") -> navBackStackEntry?.toRoute<SDM3Route.Home>()?.studentId
            currentRouteStr.contains("Main") -> navBackStackEntry?.toRoute<SDM3Route.Main>()?.studentId
            currentRouteStr.contains("NilaiRapor") -> navBackStackEntry?.toRoute<SDM3Route.NilaiRapor>()?.studentId
            currentRouteStr.contains("PembayaranSpp") -> navBackStackEntry?.toRoute<SDM3Route.PembayaranSpp>()?.studentId
            currentRouteStr.contains("HalamanRapor") -> navBackStackEntry?.toRoute<SDM3Route.HalamanRapor>()?.studentId
            else -> null
        }
    } catch (_: Exception) {
        null
    }
    val studentId = if (!argStudentId.isNullOrEmpty()) {
        secureTokenManager.saveSelectedStudentId(argStudentId)
        argStudentId
    } else {
        savedStudentId
    }

    Sdm3AdaptiveLayout(
        selectedTab = currentTab,
        showNav = showBottomBar,
        onTabSelected = { tab ->
            val route: SDM3Route = when (tab) {
                SDM3BottomTab.Beranda -> SDM3Route.Main(studentId)
                SDM3BottomTab.Nilai -> SDM3Route.NilaiRapor(
                    studentId,
                    secureTokenManager.getLastNilaiSemester().orEmpty()
                )
                SDM3BottomTab.Bayar -> SDM3Route.PembayaranSpp(studentId)
                SDM3BottomTab.Rapor -> SDM3Route.HalamanRapor(studentId)
                SDM3BottomTab.Profil -> SDM3Route.ProfilAkun
            }
            navController.navigate(route) {
                popUpTo<SDM3Route.Main> {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { 
                slideInHorizontally(
                    initialOffsetX = { it }, 
                    animationSpec = tween(500, easing = PremiumEasing)
                ) + fadeIn(animationSpec = tween(500)) 
            },
            exitTransition = { 
                slideOutHorizontally(
                    targetOffsetX = { -it / 4 }, 
                    animationSpec = tween(500, easing = PremiumEasing)
                ) + fadeOut(animationSpec = tween(500)) 
            },
            popEnterTransition = { 
                slideInHorizontally(
                    initialOffsetX = { -it / 4 }, 
                    animationSpec = tween(500, easing = PremiumEasing)
                ) + fadeIn(animationSpec = tween(500)) 
            },
            popExitTransition = { 
                slideOutHorizontally(
                    targetOffsetX = { it }, 
                    animationSpec = tween(500, easing = PremiumEasing)
                ) + fadeOut(animationSpec = tween(500)) 
            }
        ) {

            composable<SDM3Route.Splash>(
                enterTransition = { fadeIn(animationSpec = tween(400)) },
                exitTransition = { fadeOut(animationSpec = tween(400)) },
                popEnterTransition = { fadeIn(animationSpec = tween(400)) },
                popExitTransition = { fadeOut(animationSpec = tween(400)) }
            ) {
                SplashScreen(onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo<SDM3Route.Splash> { inclusive = true }
                    }
                })
            }

            composable<SDM3Route.Onboarding>(
                enterTransition = { fadeIn(animationSpec = tween(400)) },
                exitTransition = { fadeOut(animationSpec = tween(400)) },
                popEnterTransition = { fadeIn(animationSpec = tween(400)) },
                popExitTransition = { fadeOut(animationSpec = tween(400)) }
            ) {
                val secureTokenManager: com.sdm3.parent.core.security.SecureTokenManager = org.koin.compose.koinInject()
                OnboardingScreen(onComplete = {
                    secureTokenManager.setOnboardingCompleted(true)
                    navController.navigate(SDM3Route.Login) {
                        popUpTo<SDM3Route.Onboarding> { inclusive = true }
                    }
                })
            }

            composable<SDM3Route.Login>(
                enterTransition = { fadeIn(animationSpec = tween(400)) },
                exitTransition = { fadeOut(animationSpec = tween(400)) },
                popEnterTransition = { fadeIn(animationSpec = tween(400)) },
                popExitTransition = { fadeOut(animationSpec = tween(400)) }
            ) {
                val loginViewModel: LoginViewModel = koinViewModel()
                val secureTokenManager: com.sdm3.parent.core.security.SecureTokenManager = org.koin.compose.koinInject()
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        val studentId = secureTokenManager.getSelectedStudentId()
                        if (!studentId.isNullOrBlank()) {
                            navController.navigate(SDM3Route.Main(studentId)) {
                                popUpTo<SDM3Route.Login> { inclusive = true }
                            }
                        } else {
                            navController.navigate(SDM3Route.PilihAnak) {
                                popUpTo<SDM3Route.Login> { inclusive = true }
                            }
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

            composable<SDM3Route.PilihAnak>(
                enterTransition = { fadeIn(animationSpec = tween(400)) },
                exitTransition = { fadeOut(animationSpec = tween(400)) },
                popEnterTransition = { fadeIn(animationSpec = tween(400)) },
                popExitTransition = { fadeOut(animationSpec = tween(400)) }
            ) {
                val secureTokenManager: com.sdm3.parent.core.security.SecureTokenManager = org.koin.compose.koinInject()
                PilihAnakScreen(
                    onChildSelected = { studentId ->
                        secureTokenManager.saveSelectedStudentId(studentId)
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
                    onBack = null,
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
                    onBack = null,
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
                    onLanjutkan = { paymentId ->
                        navController.navigate(SDM3Route.ProsesPembayaran(paymentId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable<SDM3Route.ProsesPembayaran> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.ProsesPembayaran>()
                ProsesPembayaranScreen(
                    paymentId = route.paymentId,
                    onPembayaranBerhasil = {
                        navController.navigate(SDM3Route.PembayaranBerhasil(route.paymentId)) {
                            popUpTo<SDM3Route.ProsesPembayaran> { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
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
                        navController.navigate(SDM3Route.Main(savedStudentId)) {
                            popUpTo(0) { inclusive = true }
                        }
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
                    onBack = null,
                    onPreviewClick = { raporId, url ->
                        navController.navigate(SDM3Route.PreviewRaporPdf(raporId, url))
                    },
                    onVerifikasiClick = { raporId ->
                        navController.navigate(SDM3Route.VerifikasiQrRapor(raporId))
                    }
                )
            }

            composable<SDM3Route.DetailInfoAnak> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.DetailInfoAnak>()
                DetailInfoAnakScreen(
                    studentId = route.studentId,
                    onBack = { navController.popBackStack() },
                    onQuickNavClick = { targetRoute ->
                        navController.navigate(targetRoute)
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
                NotifikasiScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable<SDM3Route.PengumumanSekolah> {
                 PengumumanSekolahScreen(
                    onBack = { navController.popBackStack() },
                    onDetailClick = { id ->
                        navController.navigate(SDM3Route.DetailPengumuman(id))
                    }
                )
            }

            composable<SDM3Route.DetailPengumuman> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.DetailPengumuman>()
                DetailPengumumanScreen(
                    announcementId = route.announcementId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<SDM3Route.ProfilAkun> {
                ProfilAkunScreen(
                    selectedStudentId = studentId,
                    onSwitchStudent = { newId ->
                        secureTokenManager.saveSelectedStudentId(newId)
                        navController.navigate(SDM3Route.Main(newId)) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNotifikasiSetting = {
                        navController.navigate(SDM3Route.PengaturanNotifikasi)
                    },
                    onAccountDeletion = {
                        navController.navigate(SDM3Route.AccountDeletion)
                    },
                    onLogout = {
                        navController.navigate(SDM3Route.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable<SDM3Route.PengaturanNotifikasi> {
                PengaturanNotifikasiScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable<SDM3Route.AccountDeletion> {
                AccountDeletionScreen(
                    onDeletionRequestSubmitted = {
                        navController.navigate(SDM3Route.Login) {
                            popUpTo<SDM3Route.AccountDeletion> { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable<SDM3Route.PreviewRaporPdf> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.PreviewRaporPdf>()
                PreviewRaporPdfScreen(
                    raporId = route.raporId,
                    downloadUrl = route.downloadUrl,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<SDM3Route.VerifikasiQrRapor> { backStackEntry ->
                val route = backStackEntry.toRoute<SDM3Route.VerifikasiQrRapor>()
                VerifikasiQrRaporScreen(
                    raporId = route.raporId,
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
