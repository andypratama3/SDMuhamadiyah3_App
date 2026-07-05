package com.sdm3.parent.platform

import platform.Foundation.NSNotificationCenter

internal const val SDM3_QR_SCAN_NOTIFICATION = "SDM3QrScanRequest"

object IosQrScanCallbackDispatcher {
    private var pending: ((String?) -> Unit)? = null

    fun requestScan(callback: (String?) -> Unit) {
        pending = callback
        NSNotificationCenter.defaultCenter.postNotificationName(
            SDM3_QR_SCAN_NOTIFICATION,
            `object` = null,
        )
    }

    fun deliverResult(value: String?) {
        val callback = pending
        pending = null
        callback?.invoke(value?.takeIf { it.isNotBlank() })
    }
}

internal fun launchIosQrScan(onResult: (String?) -> Unit) {
    IosQrScanCallbackDispatcher.requestScan(onResult)
}
