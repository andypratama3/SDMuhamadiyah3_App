package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.ExtracurricularApi
import com.sdm3.parent.data.remote.dto.ExtracurricularDto

class ExtracurricularRepository(private val api: ExtracurricularApi) {

    suspend fun getExtracurriculars(studentId: String): ApiResult<List<ExtracurricularDto>> {
        return try {
            api.getExtracurriculars(studentId)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data ekstrakurikuler"))
        }
    }
}
