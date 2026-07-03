package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AcademicProgramDto(
    val name: String,
    val subtitle: String,
    val progress: Int,
    val target: Int,
    val coach: String,
    @SerialName("last_activity")
    val lastActivity: String
)
