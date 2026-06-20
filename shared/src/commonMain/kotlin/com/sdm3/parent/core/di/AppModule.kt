package com.sdm3.parent.core.di

import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.notification.FcmTokenProvider
import com.sdm3.parent.core.security.BiometricAuthenticator
import com.sdm3.parent.core.security.CertificatePins
import com.sdm3.parent.core.security.KVaultSecureStorage
import com.sdm3.parent.core.security.SecureTokenManager
import com.sdm3.parent.data.remote.api.ArticleApi
import com.sdm3.parent.data.remote.api.AttendanceApi
import com.sdm3.parent.data.remote.api.AuthApi
import com.sdm3.parent.data.remote.api.DashboardApi
import com.sdm3.parent.data.remote.api.ExtracurricularApi
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
import com.sdm3.parent.data.repository.StudentRepository
import com.sdm3.parent.feature.auth.LoginViewModel
import com.sdm3.parent.feature.auth.PilihAnakViewModel
import com.sdm3.parent.feature.auth.VerifikasiOtpViewModel
import com.sdm3.parent.feature.home.HomeViewModel
import com.sdm3.parent.feature.infoanak.DetailInfoAnakViewModel
import com.sdm3.parent.feature.kegiatan.KegiatanProgramViewModel
import com.sdm3.parent.feature.notifikasi.NotifikasiViewModel
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
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val securityModule = module {
    single { KVaultSecureStorage(kVault = get()) as com.sdm3.parent.core.security.SecureStorage }
    single { SecureTokenManager(storage = get()) }
    single { BiometricAuthenticator() }
}

val networkModule = module {
    single {
        val secureTokenManager = get<SecureTokenManager>()
        HttpClientProvider(
            baseUrl = get<SDM3Config>().baseUrl,
            tokenProvider = { secureTokenManager.getBearerToken() },
            onSessionExpired = { secureTokenManager.clearAllSecureData() },
            certificatePins = CertificatePins.pins
        )
    }
}

val configModule = module {
    single { SDM3Config() }
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
}

val repositoryModule = module {
    single<com.sdm3.parent.data.repository.AuthRepositoryContract> { AuthRepository(get()) }
    single { StudentRepository(get()) }
    single { GradeRepository(get()) }
    single { AttendanceRepository(get()) }
    single { PaymentRepository(get()) }
    single { NotificationRepository(get()) }
    single { ArticleRepository(get()) }
    single { RaporRepository(get()) }
    single { DashboardRepository(get()) }
    single { ProfileRepository(get()) }
    single { ExtracurricularRepository(get()) }
}

val viewModelModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::PilihAnakViewModel)
    viewModelOf(::VerifikasiOtpViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::PembayaranSppViewModel)
    viewModelOf(::PilihMetodeBayarViewModel)
    viewModelOf(::ProsesPembayaranViewModel)
    viewModelOf(::PembayaranBerhasilViewModel)
    viewModelOf(::DetailBuktiBayarViewModel)
    viewModelOf(::NotifikasiViewModel)
    viewModelOf(::PengumumanSekolahViewModel)
    viewModelOf(::ProfilAkunViewModel)
    viewModelOf(::PengaturanNotifikasiViewModel)
    viewModelOf(::DetailInfoAnakViewModel)
    viewModelOf(::KegiatanProgramViewModel)
    viewModelOf(::NilaiRaporViewModel)
    viewModelOf(::DetailNilaiMapelViewModel)
    viewModelOf(::HalamanRaporViewModel)
    viewModelOf(::KehadiranSiswaViewModel)
    viewModelOf(::PreviewRaporPdfViewModel)
    viewModelOf(::VerifikasiQrRaporViewModel)
}

val notificationModule = module {
    single { FcmTokenProvider() }
}

data class SDM3Config(
    val baseUrl: String = "https://admin.sdm3.sch.id"
)
