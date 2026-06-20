package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String,
    val type: String,
    val title: String,
    val message: String,
    val data: Map<String, String>? = null,
    @SerialName("read_at")
    val readAt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)
