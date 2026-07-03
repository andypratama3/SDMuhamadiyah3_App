package com.sdm3.parent.domain.repository

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.GradeComponentDto
import com.sdm3.parent.data.remote.dto.GradeDto
import com.sdm3.parent.data.remote.dto.TranscriptDto

interface GradeRepositoryContract {
    suspend fun getGrades(studentId: String, semester: String? = null): ApiResult<List<GradeDto>>
    suspend fun getGradeComponents(studentId: String, subjectId: String): ApiResult<List<GradeComponentDto>>
    suspend fun getTranscript(studentId: String): ApiResult<TranscriptDto>
}
