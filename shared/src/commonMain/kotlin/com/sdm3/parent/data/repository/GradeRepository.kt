package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.di.DevMode
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.dummy.DummyDataProvider
import com.sdm3.parent.data.remote.api.GradeApi
import com.sdm3.parent.data.remote.dto.GradeComponentDto
import com.sdm3.parent.data.remote.dto.GradeDto
import com.sdm3.parent.domain.repository.GradeRepositoryContract

class GradeRepository(
    private val api: GradeApi,
    private val cache: CacheDataSource,
) : GradeRepositoryContract {

    override suspend fun getGrades(studentId: String, semester: String?): ApiResult<List<GradeDto>> {
        return try {
            val result = api.getGrades(studentId, semester)
            if (result is ApiResult.Success) cache.cacheGrades(studentId, result.data)
            result
        } catch (e: Exception) {
            val semesterFilter = semester ?: ""
            val cached = if (semesterFilter.isNotEmpty()) cache.getGradesBySemester(studentId, semesterFilter)
                          else cache.getGrades(studentId)
            if (cached.isNotEmpty()) ApiResult.Success(cached)
            else if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummyGrades)
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data nilai"))
        }
    }

    override suspend fun getGradeComponents(studentId: String, subjectId: String): ApiResult<List<GradeComponentDto>> {
        return try {
            val result = api.getGradeComponents(studentId, subjectId)
            if (result is ApiResult.Success) cache.cacheGradeComponents(studentId, result.data)
            result
        } catch (e: Exception) {
            val cached = cache.getGradeComponents(subjectId, studentId)
            if (cached.isNotEmpty()) ApiResult.Success(cached)
            else if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummyGradeComponents)
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil komponen nilai"))
        }
    }
}
