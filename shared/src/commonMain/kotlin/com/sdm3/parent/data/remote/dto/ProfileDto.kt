package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val avatar: String? = null
)
