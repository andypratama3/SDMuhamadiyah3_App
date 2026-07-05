package com.sdm3.parent.core.notification

import com.sdm3.parent.core.navigation.SDM3Route
import com.sdm3.parent.core.security.SecureTokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet

data class PushDeepLink(
    val type: String,
    val studentId: String? = null,
    val paymentId: String? = null,
    val feeId: String? = null,
    val articleId: String? = null,
    val notificationId: String? = null,
    val semester: String? = null,
    val subjectId: String? = null,
)

object PushDeepLinkHolder {
    private val _pending = MutableStateFlow<PushDeepLink?>(null)
    val pending: StateFlow<PushDeepLink?> = _pending.asStateFlow()

    fun setFromMap(data: Map<String, String>) {
        val type = data["notification_type"]
            ?: data["type"]
            ?: return
        _pending.value = PushDeepLink(
            type = type,
            studentId = data["student_id"]?.takeIf { it.isNotBlank() },
            paymentId = data["payment_id"]?.takeIf { it.isNotBlank() },
            feeId = data["fee_id"]?.takeIf { it.isNotBlank() }
                ?: data["student_fee_id"]?.takeIf { it.isNotBlank() },
            articleId = data["article_id"]?.takeIf { it.isNotBlank() }
                ?: data["announcement_id"]?.takeIf { it.isNotBlank() },
            notificationId = data["notification_id"]?.takeIf { it.isNotBlank() },
            semester = data["semester"]?.takeIf { it.isNotBlank() },
            subjectId = data["subject_id"]?.takeIf { it.isNotBlank() },
        )
    }

    fun consume(): PushDeepLink? = _pending.updateAndGet { null }
}

object PushDeepLinkNavigator {
    fun toRoute(link: PushDeepLink, tokenManager: SecureTokenManager): SDM3Route? {
        link.studentId?.let { tokenManager.saveSelectedStudentId(it) }
        val studentId = link.studentId ?: tokenManager.getSelectedStudentId() ?: return null

        return when (link.type.lowercase()) {
            "grade", "nilai", "akademik", "academic" -> {
                val semester = link.semester?.takeIf { it.isNotBlank() }
                    ?: tokenManager.getLastNilaiSemester()
                    ?: ""
                if (link.subjectId != null) {
                    SDM3Route.DetailNilaiMapel(studentId, link.subjectId, semester)
                } else {
                    SDM3Route.NilaiRapor(studentId, semester)
                }
            }
            "payment", "payment_receipt", "keuangan", "spp", "finance", "tagihan", "invoice", "billing" -> {
                link.paymentId?.let { SDM3Route.DetailBuktiBayar(it) }
                    ?: link.feeId?.let { SDM3Route.PilihMetodeBayar(it) }
                    ?: SDM3Route.PembayaranSpp(studentId)
            }
            "attendance", "kehadiran", "leave", "izin" -> SDM3Route.KehadiranSiswa(studentId)
            "announcement", "pengumuman", "general", "info", "broadcast", "umum" -> {
                val id = link.articleId ?: link.notificationId ?: return null
                SDM3Route.DetailPengumuman(id)
            }
            "rapor", "report" -> SDM3Route.HalamanRapor(studentId)
            else -> SDM3Route.Main(studentId)
        }
    }

    fun isAuthenticatedDestination(route: String): Boolean =
        route.contains("Splash").not() &&
            route.contains("Onboarding").not() &&
            route.contains("Login").not() &&
            route.contains("VerifikasiOtp").not() &&
            route.contains("AccountDeletion").not()
}
