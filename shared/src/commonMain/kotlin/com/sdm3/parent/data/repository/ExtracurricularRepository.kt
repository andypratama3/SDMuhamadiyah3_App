package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.safeApiCall
import com.sdm3.parent.core.network.safeApiCallWithCache
import com.sdm3.parent.data.remote.api.ExtracurricularApi
import com.sdm3.parent.data.remote.dto.AcademicProgramDto
import com.sdm3.parent.data.remote.dto.ExtracurricularDto
import com.sdm3.parent.domain.repository.ExtracurricularRepositoryContract

class ExtracurricularRepository(
    private val api: ExtracurricularApi,
    private val cache: CacheDataSource,
) : ExtracurricularRepositoryContract {

    override suspend fun getExtracurriculars(studentId: String): ApiResult<List<ExtracurricularDto>> =
        safeApiCallWithCache(
            fallback = "Gagal mengambil data ekstrakurikuler",
            block = {
                when (val result = api.getExtracurriculars(studentId)) {
                    is ApiResult.Success -> {
                        cache.cacheExtracurriculars(result.data)
                        result
                    }
                    is ApiResult.Error -> result
                }
            },
            cacheFallback = { cache.getExtracurriculars().takeIf { it.isNotEmpty() } },
        )

    override suspend fun getAcademicPrograms(studentId: String): ApiResult<List<AcademicProgramDto>> =
        safeApiCall("Gagal mengambil data program unggulan") { api.getAcademicPrograms(studentId) }
}
