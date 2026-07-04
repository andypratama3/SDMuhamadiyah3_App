package com.sdm3.parent.platform

expect object PlatformActions {
    fun copyToClipboard(text: String, label: String = "Copied")
    fun shareText(text: String, title: String? = null)
    fun openUrl(url: String)
    suspend fun scanQrCode(): String?

    /** Apakah pemindaian QR via kamera didukung di platform ini. */
    fun isQrScanSupported(): Boolean
}
