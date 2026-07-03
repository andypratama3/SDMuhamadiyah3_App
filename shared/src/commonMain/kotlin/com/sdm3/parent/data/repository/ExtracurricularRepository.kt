package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.ExtracurricularApi
import com.sdm3.parent.data.remote.dto.AcademicProgramDto
import com.sdm3.parent.data.remote.dto.ExtracurricularDto
import com.sdm3.parent.domain.repository.ExtracurricularRepositoryContract

class ExtracurricularRepository(
    private val api: ExtracurricularApi,
    private val cache: CacheDataSource,
) : ExtracurricularRepositoryContract {

    override suspend fun getExtracurriculars(studentId: String): ApiResult<List<ExtracurricularDto>> {
        return try {
            val result = api.getExtracurriculars(studentId)
            if (result is ApiResult.Success) cache.cacheExtracurriculars(result.data)
            result
        } catch (e: Exception) {
            val cached = cache.getExtracurriculars()
            if (cached.isNotEmpty()) ApiResult.Success(cached)
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data ekstrakurikuler"))
        }
    }

    override suspend fun getAcademicPrograms(studentId: String): ApiResult<List<AcademicProgramDto>> {
        return try {
            api.getAcademicPrograms(studentId)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data program unggulan"))
        }
    }
}
