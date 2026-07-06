package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.safeApiCall
import com.sdm3.parent.core.network.safeApiCallWithCache
import com.sdm3.parent.data.remote.api.GradeApi
import com.sdm3.parent.data.remote.dto.GradeComponentDto
import com.sdm3.parent.data.remote.dto.GradeDto
import com.sdm3.parent.data.remote.dto.TranscriptDto
import com.sdm3.parent.domain.repository.GradeRepositoryContract

class GradeRepository(
    private val api: GradeApi,
    private val cache: CacheDataSource,
) : GradeRepositoryContract {

    override suspend fun getGrades(studentId: String, semester: String?): ApiResult<List<GradeDto>> =
        safeApiCallWithCache(
            fallback = "Gagal mengambil data nilai",
            block = {
                when (val result = api.getGrades(studentId, semester)) {
                    is ApiResult.Success -> {
                        cache.cacheGrades(studentId, result.data)
                        result
                    }
                    is ApiResult.Error -> result
                }
            },
            cacheFallback = {
                val semesterFilter = semester.orEmpty()
                val cached = if (semesterFilter.isNotEmpty()) {
                    cache.getGradesBySemester(studentId, semesterFilter)
                } else {
                    cache.getGrades(studentId)
                }
                cached.takeIf { it.isNotEmpty() }
            },
        )

    override suspend fun getGradeComponents(studentId: String, subjectId: String): ApiResult<List<GradeComponentDto>> =
        safeApiCallWithCache(
            fallback = "Gagal mengambil komponen nilai",
            block = {
                when (val result = api.getGradeComponents(studentId, subjectId)) {
                    is ApiResult.Success -> {
                        cache.cacheGradeComponents(studentId, result.data)
                        result
                    }
                    is ApiResult.Error -> result
                }
            },
            cacheFallback = {
                cache.getGradeComponents(subjectId, studentId).takeIf { it.isNotEmpty() }
            },
        )

    override suspend fun getTranscript(studentId: String): ApiResult<TranscriptDto> =
        safeApiCall("Gagal mengambil transkrip nilai") { api.getTranscript(studentId) }
}
