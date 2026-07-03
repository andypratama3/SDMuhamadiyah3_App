package com.sdm3.parent.platform

import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.sdm3.parent.core.security.AndroidBiometricProvider

class AndroidPlatformBootstrap(private val activity: AppCompatActivity) {

    private var pendingQrCallback: ((String?) -> Unit)? = null

    private val qrScanLauncher = activity.registerForActivityResult(ScanContract()) { result ->
        pendingQrCallback?.invoke(result.contents)
        pendingQrCallback = null
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
    }

    fun clear() {
        if (AndroidBiometricProvider.activity === activity) {
            AndroidBiometricProvider.activity = null
        }
        if (AndroidPlatformProvider.activity === activity) {
            AndroidPlatformProvider.activity = null
            AndroidPlatformProvider.launchQrScan = null
        }
    }
}

fun AppCompatActivity.installAndroidPlatformBindings(): AndroidPlatformBootstrap {
    return AndroidPlatformBootstrap(this).also { it.install() }
}
