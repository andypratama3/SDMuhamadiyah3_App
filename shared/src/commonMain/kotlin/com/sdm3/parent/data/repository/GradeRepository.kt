package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.GradeApi
import com.sdm3.parent.data.remote.dto.GradeComponentDto
import com.sdm3.parent.data.remote.dto.GradeDto

class GradeRepository(private val api: GradeApi) {

    suspend fun getGrades(studentId: String, semester: String? = null): ApiResult<List<GradeDto>> {
        return try {
            api.getGrades(studentId, semester)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data nilai"))
        }
    }

    suspend fun getGradeComponents(studentId: String, subjectId: String): ApiResult<List<GradeComponentDto>> {
        return try {
            api.getGradeComponents(studentId, subjectId)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil komponen nilai"))
        }
    }
}
