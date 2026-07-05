package com.sdm3.parent.platform

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.sdm3.parent.core.security.AndroidBiometricProvider

class AndroidPlatformBootstrap(private val activity: AppCompatActivity) {

    private var pendingQrCallback: ((String?) -> Unit)? = null
    private var pendingAvatarCallback: ((PickedImage?) -> Unit)? = null

    private val qrScanLauncher = activity.registerForActivityResult(ScanContract()) { result ->
        pendingQrCallback?.invoke(result.contents)
        pendingQrCallback = null
    }

    private val pickAvatarLauncher = activity.registerForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri ->
        pendingAvatarCallback?.invoke(uri?.let(::readPickedImage))
        pendingAvatarCallback = null
    }

    fun install() {
        AndroidBiometricProvider.activity = activity
        AndroidPlatformProvider.activity = activity
        AndroidPlatformProvider.launchQrScan = { callback ->
            pendingQrCallback = callback
            qrScanLauncher.launch(
                ScanOptions().apply {
                    setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                    setPrompt("Arahkan kamera ke QR Code rapor")
                    setBeepEnabled(false)
                    setBarcodeImageEnabled(false)
                    setOrientationLocked(false)
                }
            )
        }
        AndroidPlatformProvider.launchPickAvatar = { callback ->
            pendingAvatarCallback = callback
            pickAvatarLauncher.launch("image/*")
        }
    }

    private fun readPickedImage(uri: Uri): PickedImage? {
        val resolver = activity.contentResolver
        val mimeType = resolver.getType(uri) ?: "image/jpeg"
        val extension = when {
            mimeType.contains("png", ignoreCase = true) -> "png"
            mimeType.contains("webp", ignoreCase = true) -> "webp"
            else -> "jpg"
        }
        val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
        if (bytes.isEmpty()) return null
        return PickedImage(
            bytes = bytes,
            fileName = "avatar.$extension",
            mimeType = mimeType,
        )
    }

    fun clear() {
        if (AndroidBiometricProvider.activity === activity) {
            AndroidBiometricProvider.activity = null
        }
        if (AndroidPlatformProvider.activity === activity) {
            AndroidPlatformProvider.activity = null
            AndroidPlatformProvider.launchQrScan = null
            AndroidPlatformProvider.launchPickAvatar = null
        }
    }
}

fun AppCompatActivity.installAndroidPlatformBindings(): AndroidPlatformBootstrap {
    return AndroidPlatformBootstrap(this).also { it.install() }
}
