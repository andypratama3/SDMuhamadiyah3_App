package com.sdm3.parent.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIPasteboard

actual object PlatformActions {

    actual fun copyToClipboard(text: String, label: String) {
        UIPasteboard.generalPasteboard.string = text
    }

    actual fun shareText(text: String, title: String?) {
        val rootController = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return
        val items = listOf(text)
        val controller = UIActivityViewController(activityItems = items, applicationActivities = null)
        rootController.presentViewController(controller, animated = true, completion = null)
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun openUrl(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        UIApplication.sharedApplication.openURL(nsUrl)
    }

    actual suspend fun scanQrCode(): String? = null

    // Pemindaian QR via kamera belum tersedia di iOS; pakai input manual.
    actual fun isQrScanSupported(): Boolean = false
}
