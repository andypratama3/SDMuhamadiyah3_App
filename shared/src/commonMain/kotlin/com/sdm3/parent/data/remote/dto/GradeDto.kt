package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GradeDto(
    val id: String,
    @SerialName("subject_id")
    val subjectId: String,
    @SerialName("subject_name")
    val subjectName: String,
    val score: Double? = null,
    val predicate: String? = null,
    val narrative: String? = null,
    val semester: String
)

@Serializable
data class GradeComponentDto(
    val id: String,
    @SerialName("subject_id")
    val subjectId: String,
    @SerialName("subject_name")
    val subjectName: String,
    @SerialName("component_type")
    val componentType: String,
    @SerialName("component_subtype")
    val componentSubtype: String? = null,
    val score: Double? = null,
    @SerialName("tp_name")
    val tpName: String? = null,
    @SerialName("tp_number")
    val tpNumber: Int? = null
)

@Serializable
data class GradeWeightDto(
    val id: String,
    @SerialName("subject_id")
    val subjectId: String,
    @SerialName("component_label")
    val componentLabel: String,
    val percentage: Double,
    val kkm: Double
)
