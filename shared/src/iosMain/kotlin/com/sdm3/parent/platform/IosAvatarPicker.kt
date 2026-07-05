package com.sdm3.parent.platform

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSNotificationCenter

internal const val SDM3_AVATAR_PICK_NOTIFICATION = "SDM3AvatarPickRequest"

object IosAvatarPickCallbackDispatcher {
    private var pending: ((PickedImage?) -> Unit)? = null

    fun requestPick(callback: (PickedImage?) -> Unit) {
        pending = callback
        NSNotificationCenter.defaultCenter.postNotificationName(
            SDM3_AVATAR_PICK_NOTIFICATION,
            `object` = null,
        )
    }

    @OptIn(ExperimentalForeignApi::class, ExperimentalEncodingApi::class)
    fun deliverResult(base64Data: String?, fileName: String?, mimeType: String?) {
        val callback = pending
        pending = null
        if (base64Data.isNullOrBlank()) {
            callback?.invoke(null)
            return
        }
        val bytes = try {
            Base64.decode(base64Data)
        } catch (_: Exception) {
            callback?.invoke(null)
            return
        }
        if (bytes.isEmpty()) {
            callback?.invoke(null)
            return
        }
        callback?.invoke(
            PickedImage(
                bytes = bytes,
                fileName = fileName?.takeIf { it.isNotBlank() } ?: "avatar.jpg",
                mimeType = mimeType?.takeIf { it.isNotBlank() } ?: "image/jpeg",
            ),
        )
    }
}

internal fun launchIosAvatarPick(onResult: (PickedImage?) -> Unit) {
    IosAvatarPickCallbackDispatcher.requestPick(onResult)
}
