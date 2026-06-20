package com.sdm3.parent.domain.entity

data class Grade(
    val id: String,
    val studentId: String,
    val subjectId: String,
    val subjectName: String,
    val classroomId: String,
    val academicYear: String,
    val semester: String,
    val score: Double?,
    val narrative: String?,
    val predicate: String?,
    val isManualOverride: Boolean,
    val components: List<GradeComponent> = emptyList()
)

data class GradeComponent(
    val id: String,
    val subjectId: String,
    val componentType: ComponentType,
    val componentSubtype: String?,
    val score: Double?,
    val tpName: String?,
    val tpNumber: Int?,
    val isMaxTp: Boolean,
    val isMinTp: Boolean
)

enum class ComponentType { SUMATIF, FORMATIF, PROJEK }
