package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentDto(
    val id: String,
    val name: String,
    val nisn: String? = null,
    val nis: String? = null,
    val gender: String,
    @SerialName("birth_place")
    val birthPlace: String? = null,
    @SerialName("birth_date")
    val birthDate: String? = null,
    val photo: String? = null,
    val status: String? = null,
    @SerialName("class_name")
    val className: String? = null,
    val classroom: StudentClassroomDto? = null,
    @SerialName("wali_kelas")
    val waliKelas: String? = null,
    @SerialName("portal_id")
    val portalId: String? = null,
    val spp: Int? = null,
    val dpp: Int? = null
)

@Serializable
data class StudentClassroomDto(
    val id: String,
    val name: String,
    @SerialName("grade_label")
    val gradeLabel: String? = null
)
