/*
 * SDM3 Parent Portal — Notification Permission (Android 13+ & iOS)
 *
 * KENAPA INI WAJIB ADA TAPI TIDAK DISEBUT DI BLUEPRINT ASLI:
 *
 * Android 13 (API 33) ke atas: notifikasi TIDAK akan tampil sama sekali
 * kecuali app secara eksplisit minta izin runtime POST_NOTIFICATIONS.
 * Tanpa ini, fitur Notifikasi & Pengumuman (Phase 7) di blueprint akan
 * terasa "rusak" di device Android 13+ tanpa ada pesan error apapun.
 *
 * iOS: setiap permintaan push notification HARUS lewat prompt sistem
 * (UNUserNotificationCenter.requestAuthorization). App Store review akan
 * reject app yang mendaftarkan untuk remote notification tanpa permission
 * flow yang jelas ke user.
 */

// =============================================================================
// commonMain — kontrak yang sama dipakai Android & iOS
// =============================================================================
package com.sdm3.parent.core.permission

/** Status izin notifikasi, dipetakan dari hasil native masing-masing platform. */
enum class NotificationPermissionStatus {
    GRANTED,
    DENIED,
    /** Belum pernah ditanyakan — Android: belum pernah request; iOS: notDetermined */
    NOT_REQUESTED
}

/**
 * Kontrak expect/actual. Implementasi nyata ada di androidMain & iosMain
 * (lihat di bawah). Disuntik lewat Koin sebagai `single`.
 */
expect class NotificationPermissionController {
    suspend fun checkStatus(): NotificationPermissionStatus
    suspend fun requestPermission(): NotificationPermissionStatus
}

/*
 * =============================================================================
 * androidMain/kotlin/.../NotificationPermissionController.android.kt
 * =============================================================================
 *
 * package com.sdm3.parent.core.permission
 *
 * import android.Manifest
 * import android.content.Context
 * import android.content.pm.PackageManager
 * import android.os.Build
 * import androidx.core.content.ContextCompat
 *
 * actual class NotificationPermissionController(
 *     private val context: Context,
 *     // Disuntik dari Activity lewat ActivityResultContracts.RequestPermission()
 *     // — implementasi penuhnya butuh bridging ke Activity, biasanya lewat
 *     // callback yang didaftarkan saat MainActivity.onCreate().
 *     private val requestPermissionLauncher: suspend () -> Boolean
 * ) {
 *     actual suspend fun checkStatus(): NotificationPermissionStatus {
 *         // Android < 13 tidak butuh runtime permission untuk notifikasi biasa.
 *         if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
 *             return NotificationPermissionStatus.GRANTED
 *         }
 *         val granted = ContextCompat.checkSelfPermission(
 *             context, Manifest.permission.POST_NOTIFICATIONS
 *         ) == PackageManager.PERMISSION_GRANTED
 *         return if (granted) NotificationPermissionStatus.GRANTED
 *                else NotificationPermissionStatus.NOT_REQUESTED
 *     }
 *
 *     actual suspend fun requestPermission(): NotificationPermissionStatus {
 *         if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
 *             return NotificationPermissionStatus.GRANTED
 *         }
 *         val granted = requestPermissionLauncher()
 *         return if (granted) NotificationPermissionStatus.GRANTED
 *                else NotificationPermissionStatus.DENIED
 *     }
 * }
 *
 * // Di MainActivity.kt:
 * // val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> ... }
 * // launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
 *
 * // AndroidManifest.xml — WAJIB ditambahkan:
 * // <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
 *
 * =============================================================================
 * iosMain/kotlin/.../NotificationPermissionController.ios.kt
 * =============================================================================
 *
 * package com.sdm3.parent.core.permission
 *
 * import kotlinx.coroutines.suspendCancellableCoroutine
 * import platform.UserNotifications.*
 * import kotlin.coroutines.resume
 *
 * actual class NotificationPermissionController {
 *
 *     actual suspend fun checkStatus(): NotificationPermissionStatus =
 *         suspendCancellableCoroutine { cont ->
 *             UNUserNotificationCenter.currentNotificationCenter()
 *                 .getNotificationSettingsWithCompletionHandler { settings ->
 *                     val status = when (settings?.authorizationStatus) {
 *                         UNAuthorizationStatusAuthorized -> NotificationPermissionStatus.GRANTED
 *                         UNAuthorizationStatusDenied -> NotificationPermissionStatus.DENIED
 *                         else -> NotificationPermissionStatus.NOT_REQUESTED
 *                     }
 *                     cont.resume(status)
 *                 }
 *         }
 *
 *     actual suspend fun requestPermission(): NotificationPermissionStatus =
 *         suspendCancellableCoroutine { cont ->
 *             UNUserNotificationCenter.currentNotificationCenter()
 *                 .requestAuthorizationWithOptions(
 *                     options = UNAuthorizationOptionAlert or
 *                         UNAuthorizationOptionSound or
 *                         UNAuthorizationOptionBadge,
 *                     completionHandler = { granted, _ ->
 *                         cont.resume(
 *                             if (granted) NotificationPermissionStatus.GRANTED
 *                             else NotificationPermissionStatus.DENIED
 *                         )
 *                     }
 *                 )
 *         }
 * }
 *
 * =============================================================================
 * CARA PAKAI — sebaiknya dipanggil SEKALI, setelah user berhasil PilihAnak
 * (jangan langsung di Splash — minta izin tanpa konteks biasanya ditolak user)
 * =============================================================================
 *
 * class OnboardingCompletionUseCase(
 *     private val permissionController: NotificationPermissionController
 * ) {
 *     suspend operator fun invoke() {
 *         val status = permissionController.checkStatus()
 *         if (status == NotificationPermissionStatus.NOT_REQUESTED) {
 *             permissionController.requestPermission()
 *         }
 *     }
 * }
 */
