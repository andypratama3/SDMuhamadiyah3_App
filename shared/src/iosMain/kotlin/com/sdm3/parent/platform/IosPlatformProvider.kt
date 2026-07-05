package com.sdm3.parent.platform

object IosPlatformProvider {
    var launchQrScan: ((onResult: (String?) -> Unit) -> Unit)? = null
    var launchPickAvatar: ((onResult: (PickedImage?) -> Unit) -> Unit)? = null
}
