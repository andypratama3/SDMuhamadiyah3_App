package com.sdm3.parent.platform

fun installIosPlatformBindings() {
    IosPlatformProvider.launchQrScan = { callback ->
        launchIosQrScan(callback)
    }
    IosPlatformProvider.launchPickAvatar = { callback ->
        launchIosAvatarPick(callback)
    }
}

fun clearIosPlatformBindings() {
    IosPlatformProvider.launchQrScan = null
    IosPlatformProvider.launchPickAvatar = null
}
