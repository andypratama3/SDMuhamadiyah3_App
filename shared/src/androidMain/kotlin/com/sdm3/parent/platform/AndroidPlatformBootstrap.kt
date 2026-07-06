package com.sdm3.parent.platform

import android.Manifest
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.sdm3.parent.core.security.AndroidBiometricProvider
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class AndroidPlatformBootstrap(private val activity: AppCompatActivity) {

    private var pendingQrCallback: ((String?) -> Unit)? = null
    private var pendingAvatarCallback: ((PickedImage?) -> Unit)? = null
    private var locationPermissionContinuation: ((Boolean) -> Unit)? = null

    private val locationPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        val granted = results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        locationPermissionContinuation?.invoke(granted)
        locationPermissionContinuation = null
    }

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
        AndroidPlatformProvider.requestLocationPermission = {
            if (hasLocationPermission()) {
                true
            } else {
                suspendCancellableCoroutine { continuation ->
                    AndroidPlatformProvider.locationPermissionRequested = true
                    locationPermissionContinuation = { granted ->
                        if (continuation.isActive) continuation.resume(granted)
                    }
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                        ),
                    )
                }
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
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
            AndroidPlatformProvider.requestLocationPermission = null
        }
    }
}

fun AppCompatActivity.installAndroidPlatformBindings(): AndroidPlatformBootstrap {
    return AndroidPlatformBootstrap(this).also { it.install() }
}
