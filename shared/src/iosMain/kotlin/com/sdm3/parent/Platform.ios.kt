package com.sdm3.parent

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

actual fun getPlatformName(): String = "iOS"
actual fun defaultBaseUrl(): String = "https://admin.sdm3.sch.id"

@OptIn(ExperimentalNativeApi::class)
actual fun isDebugBuild(): Boolean = Platform.isDebugBinary
