package com.sdm3.parent

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

actual fun getPlatformName(): String = "iOS"

// Semua build menunjuk ke server produksi (domain publik HTTPS).
// Untuk pengembangan lokal, ganti sementara ke "http://127.0.0.1:8000".
actual fun defaultBaseUrl(): String = "https://app.sdmuhammadiyah3smd.com"

@OptIn(ExperimentalNativeApi::class)
actual fun isDebugBuild(): Boolean = Platform.isDebugBinary
