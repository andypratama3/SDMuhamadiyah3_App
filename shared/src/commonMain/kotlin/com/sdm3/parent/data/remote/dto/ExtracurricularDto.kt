package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExtracurricularDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val schedule: String? = null,
    @SerialName("teacher_name")
    val teacherName: String? = null
)
