package com.sdm3.parent.core.di

import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.defaultBaseUrl
import com.sdm3.parent.isDebugBuild
import com.sdm3.parent.core.notification.FcmRegistrar
import com.sdm3.parent.core.notification.FcmRegistrationService
import com.sdm3.parent.core.notification.FcmTokenProvider
import com.sdm3.parent.core.auth.SessionLogoutCoordinator
import com.sdm3.parent.core.security.BiometricAuthGate
import com.sdm3.parent.core.security.BiometricAuthenticator
import com.sdm3.parent.core.security.CertificatePins
import com.sdm3.parent.core.security.KVaultSecureStorage
import com.sdm3.parent.core.security.SecureStorage
import com.sdm3.parent.core.security.SecureTokenManager
import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.cache.DatabaseDriverFactory
import com.sdm3.parent.cache.SDM3Database
import com.sdm3.parent.data.remote.api.ArticleApi
import com.sdm3.parent.data.remote.api.AttendanceApi
import com.sdm3.parent.data.remote.api.AuthApi
import com.sdm3.parent.data.remote.api.DashboardApi
import com.sdm3.parent.data.remote.api.ExtracurricularApi
import com.sdm3.parent.data.remote.api.FcmApi
import com.sdm3.parent.data.remote.api.NotificationPreferencesApi
import com.sdm3.parent.data.remote.api.TeacherAttendanceApi
import com.sdm3.parent.data.remote.api.GradeApi
import com.sdm3.parent.data.remote.api.NotificationApi
import com.sdm3.parent.data.remote.api.PaymentApi
import com.sdm3.parent.data.remote.api.ProfileApi
import com.sdm3.parent.data.remote.api.RaporApi
import com.sdm3.parent.data.remote.api.StudentApi
import com.sdm3.parent.data.repository.ArticleRepository
import com.sdm3.parent.data.repository.AttendanceRepository
import com.sdm3.parent.data.repository.AuthRepository
import com.sdm3.parent.data.repository.DashboardRepository
import com.sdm3.parent.data.repository.ExtracurricularRepository
import com.sdm3.parent.data.repository.GradeRepository
import com.sdm3.parent.data.repository.NotificationRepository
import com.sdm3.parent.data.repository.PaymentRepository
import com.sdm3.parent.data.repository.ProfileRepository
import com.sdm3.parent.data.repository.RaporRepository
import com.sdm3.parent.data.repository.SettingsRepository
import com.sdm3.parent.data.repository.StudentRepository
import com.sdm3.parent.data.repository.TeacherAttendanceRepository
import com.sdm3.parent.domain.repository.ArticleRepositoryContract
import com.sdm3.parent.domain.repository.AttendanceRepositoryContract
import com.sdm3.parent.domain.repository.AuthRepositoryContract
import com.sdm3.parent.domain.repository.DashboardRepositoryContract
import com.sdm3.parent.domain.repository.ExtracurricularRepositoryContract
import com.sdm3.parent.domain.repository.GradeRepositoryContract
import com.sdm3.parent.domain.repository.NotificationRepositoryContract
import com.sdm3.parent.domain.repository.PaymentRepositoryContract
import com.sdm3.parent.domain.repository.ProfileRepositoryContract
import com.sdm3.parent.domain.repository.RaporRepositoryContract
import com.sdm3.parent.domain.repository.SettingsRepositoryContract
import com.sdm3.parent.domain.repository.StudentRepositoryContract
import com.sdm3.parent.domain.repository.TeacherAttendanceRepositoryContract
import com.sdm3.parent.feature.auth.AccountDeletionViewModel
import com.sdm3.parent.feature.auth.LoginViewModel
import com.sdm3.parent.feature.auth.PilihAnakViewModel
import com.sdm3.parent.feature.auth.VerifikasiOtpViewModel
import com.sdm3.parent.feature.guru.GuruAbsensiViewModel
import com.sdm3.parent.feature.guru.TeacherHomeViewModel
import com.sdm3.parent.feature.home.HomeViewModel
import com.sdm3.parent.feature.infoanak.DetailInfoAnakViewModel
import com.sdm3.parent.feature.infoanak.KegiatanProgramViewModel
import com.sdm3.parent.feature.notifikasi.NotifikasiViewModel
import com.sdm3.parent.feature.notifikasi.DetailPengumumanViewModel
import com.sdm3.parent.feature.notifikasi.PengumumanSekolahViewModel
import com.sdm3.parent.feature.pembayaran.DetailBuktiBayarViewModel
import com.sdm3.parent.feature.pembayaran.PembayaranBerhasilViewModel
import com.sdm3.parent.feature.pembayaran.PembayaranSppViewModel
import com.sdm3.parent.feature.pembayaran.PilihMetodeBayarViewModel
import com.sdm3.parent.feature.pembayaran.ProsesPembayaranViewModel
import com.sdm3.parent.feature.profil.PengaturanNotifikasiViewModel
import com.sdm3.parent.feature.kehadiran.KehadiranSiswaViewModel
import com.sdm3.parent.feature.nilai.DetailNilaiMapelViewModel
import com.sdm3.parent.feature.nilai.NilaiRaporViewModel
import com.sdm3.parent.feature.profil.ProfilAkunViewModel
import com.sdm3.parent.feature.rapor.HalamanRaporViewModel
import com.sdm3.parent.feature.rapor.PreviewRaporPdfViewModel
import com.sdm3.parent.feature.rapor.VerifikasiQrRaporViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val securityModule = module {
    single<SecureStorage> { KVaultSecureStorage(kVault = get()) }
    single { SecureTokenManager(storage = get()) }
    single<BiometricAuthGate> { BiometricAuthenticator() }
}

val networkModule = module {
    single {
        val secureTokenManager = get<SecureTokenManager>()
        val cache = get<CacheDataSource>()
        val fcmRegistrar = get<FcmRegistrar>()
        HttpClientProvider(
            baseUrl = get<SDM3Config>().baseUrl,
            tokenProvider = { secureTokenManager.getBearerToken() },
            onSessionExpired = {
                runCatching { fcmRegistrar.unregisterIfNeeded() }
                secureTokenManager.clearAllSecureData()
                cache.clearAll()
            },
            certificatePins = CertificatePins.pins,
            enableLogging = isDebugBuild()
        )
    }
}

val configModule = module {
    single { SDM3Config() }
}

val databaseModule = module {
    single { SDM3Database(get<DatabaseDriverFactory>().createDriver()) }
    single { CacheDataSource(get()) }
}

val apiModule = module {
    single { AuthApi(get()) }
    single { StudentApi(get()) }
    single { GradeApi(get()) }
    single { AttendanceApi(get()) }
    single { PaymentApi(get()) }
    single { NotificationApi(get()) }
    single { ArticleApi(get()) }
    single { RaporApi(get()) }
    single { DashboardApi(get()) }
    single { ProfileApi(get()) }
    single { ExtracurricularApi(get()) }
    single { FcmApi(get()) }
    single { NotificationPreferencesApi(get()) }
    single { TeacherAttendanceApi(get()) }
}

val repositoryModule = module {
    single<AuthRepositoryContract> { AuthRepository(get(), get(), get()) }
    single { SessionLogoutCoordinator(authRepository = get(), fcmRegistrar = get()) }
    single<StudentRepositoryContract> { StudentRepository(get(), get()) }
    single<GradeRepositoryContract> { GradeRepository(get(), get()) }
    single<AttendanceRepositoryContract> { AttendanceRepository(get(), get()) }
    single<PaymentRepositoryContract> { PaymentRepository(get(), get()) }
    single<NotificationRepositoryContract> { NotificationRepository(get(), get()) }
    single<ArticleRepositoryContract> { ArticleRepository(get(), get()) }
    single<RaporRepositoryContract> { RaporRepository(get(), get()) }
    single<DashboardRepositoryContract> { DashboardRepository(get(), get()) }
    single<ProfileRepositoryContract> { ProfileRepository(get(), get(), get()) }
    single<ExtracurricularRepositoryContract> { ExtracurricularRepository(get(), get()) }
    single<SettingsRepositoryContract> { SettingsRepository(get(), get()) }
    single<TeacherAttendanceRepositoryContract> { TeacherAttendanceRepository(get()) }
}

val viewModelModule = module {
    viewModel { LoginViewModel(get(), get(), get(), get()) }
    viewModelOf(::AccountDeletionViewModel)
    viewModelOf(::PilihAnakViewModel)
    viewModelOf(::VerifikasiOtpViewModel)
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModelOf(::PembayaranSppViewModel)
    viewModel { PilihMetodeBayarViewModel(get(), get()) }
    viewModelOf(::ProsesPembayaranViewModel)
    viewModelOf(::PembayaranBerhasilViewModel)
    viewModelOf(::DetailBuktiBayarViewModel)
    viewModelOf(::NotifikasiViewModel)
    viewModelOf(::DetailPengumumanViewModel)
    viewModelOf(::PengumumanSekolahViewModel)
    viewModel { ProfilAkunViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { PengaturanNotifikasiViewModel(get(), get()) }
    viewModelOf(::DetailInfoAnakViewModel)
    viewModelOf(::KegiatanProgramViewModel)
    viewModelOf(::NilaiRaporViewModel)
    viewModelOf(::DetailNilaiMapelViewModel)
    viewModelOf(::HalamanRaporViewModel)
    viewModelOf(::KehadiranSiswaViewModel)
    viewModelOf(::PreviewRaporPdfViewModel)
    viewModelOf(::VerifikasiQrRaporViewModel)
    viewModel { TeacherHomeViewModel(get(), get()) }
    viewModel { GuruAbsensiViewModel(get()) }
}

val notificationModule = module {
    single { FcmTokenProvider() }
    single<FcmRegistrar> { FcmRegistrationService(get(), get(), get()) }
}

data class SDM3Config(
    val baseUrl: String = defaultBaseUrl()
)
