package com.sdm3.parent.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIPasteboard
import kotlin.coroutines.resume

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

    actual suspend fun scanQrCode(): String? {
        val launcher = IosPlatformProvider.launchQrScan ?: return null
        return suspendCancellableCoroutine { continuation ->
            launcher { result ->
                if (continuation.isActive) {
                    continuation.resume(result?.takeIf { it.isNotBlank() })
                }
            }
        }
    }

    actual fun isQrScanSupported(): Boolean = IosPlatformProvider.launchQrScan != null

    actual suspend fun pickAvatarImage(): PickedImage? {
        val launcher = IosPlatformProvider.launchPickAvatar ?: return null
        return suspendCancellableCoroutine { continuation ->
            launcher { result ->
                if (continuation.isActive) {
                    continuation.resume(result)
                }
            }
        }
    }
}
