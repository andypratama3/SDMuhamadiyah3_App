package com.sdm3.parent.platform

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object AndroidPlatformProvider {
    @Volatile
    var activity: AppCompatActivity? = null

    @Volatile
    var launchQrScan: ((onResult: (String?) -> Unit) -> Unit)? = null
}

actual object PlatformActions {

    actual fun copyToClipboard(text: String, label: String) {
        val context = AndroidPlatformProvider.activity ?: return
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    }

    actual fun shareText(text: String, title: String?) {
        val activity = AndroidPlatformProvider.activity ?: return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            title?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
        }
        activity.startActivity(Intent.createChooser(intent, title ?: "Bagikan"))
    }

    actual fun openUrl(url: String) {
        val activity = AndroidPlatformProvider.activity ?: return
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        activity.startActivity(intent)
    }

    actual suspend fun scanQrCode(): String? {
        val launcher = AndroidPlatformProvider.launchQrScan ?: return null
        return suspendCancellableCoroutine { continuation ->
            launcher { result ->
                if (continuation.isActive) {
                    continuation.resume(result?.takeIf { it.isNotBlank() })
                }
            }
        }
    }
}
