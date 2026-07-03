package com.sdm3.parent

expect fun getPlatformName(): String
expect fun defaultBaseUrl(): String
expect fun isDebugBuild(): Boolean

const val APP_VERSION_NAME = "1.0.0"
const val APP_VERSION_CODE = 1
