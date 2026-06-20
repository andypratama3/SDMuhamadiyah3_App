package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentDto(
    val id: String,
    val name: String,
    val nisn: String,
    val nis: String? = null,
    val gender: String,
    @SerialName("birth_place")
    val birthPlace: String,
    @SerialName("birth_date")
    val birthDate: String,
    val photo: String? = null,
    @SerialName("class_name")
    val className: String? = null,
    val spp: Int? = null,
    val dpp: Int? = null
)
