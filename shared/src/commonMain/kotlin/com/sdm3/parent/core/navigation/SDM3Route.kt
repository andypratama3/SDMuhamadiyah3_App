package com.sdm3.parent.core.navigation

import kotlinx.serialization.Serializable

enum class SDM3BottomTab {
    Beranda, Nilai, Bayar, Rapor, Profil
}

sealed interface SDM3Route {

    @Serializable
    data object Splash : SDM3Route

    @Serializable
    data object Onboarding : SDM3Route

    @Serializable
    data object Login : SDM3Route

    @Serializable
    data class VerifikasiOtp(val email: String) : SDM3Route

    @Serializable
    data object PilihAnak : SDM3Route

    @Serializable
    data class Home(val studentId: String) : SDM3Route

    @Serializable
    data class Main(val studentId: String) : SDM3Route

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
    data class DetailBuktiBayar(val paymentId: String) : SDM3Route

    @Serializable
    data class KehadiranSiswa(val studentId: String) : SDM3Route

    @Serializable
    data class HalamanRapor(val studentId: String) : SDM3Route

    @Serializable
    data class DetailInfoAnak(val studentId: String) : SDM3Route

    @Serializable
    data class KegiatanProgram(val studentId: String) : SDM3Route

    @Serializable
    data object Notifikasi : SDM3Route

    @Serializable
    data object PengumumanSekolah : SDM3Route

    @Serializable
    data class DetailPengumuman(val announcementId: String) : SDM3Route

    @Serializable
    data object ProfilAkun : SDM3Route

    @Serializable
    data object PengaturanNotifikasi : SDM3Route

    @Serializable
    data object AccountDeletion : SDM3Route

    @Serializable
    data class PreviewRaporPdf(val raporId: String, val downloadUrl: String) : SDM3Route

    @Serializable
    data class VerifikasiQrRapor(val raporId: String) : SDM3Route
}
