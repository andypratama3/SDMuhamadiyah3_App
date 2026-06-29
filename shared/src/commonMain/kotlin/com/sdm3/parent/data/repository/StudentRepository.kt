package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.di.DevMode
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.dummy.DummyDataProvider
import com.sdm3.parent.data.remote.api.StudentApi
import com.sdm3.parent.data.remote.dto.StudentDto
import com.sdm3.parent.domain.repository.StudentRepositoryContract

open class StudentRepository(
    private val api: StudentApi?,
    private val cache: CacheDataSource,
) : StudentRepositoryContract {

    override suspend fun getStudents(): ApiResult<List<StudentDto>> {
        return try {
            when (val result = api?.getStudents() ?: ApiResult.Success(emptyList())) {
                is ApiResult.Success -> {
                    cache.cacheStudents(result.data)
                    result
                }
                is ApiResult.Error -> fallbackGetStudents(result.error)
            }
        } catch (e: Exception) {
            fallbackGetStudents(ApiError.Unknown(e.message ?: "Gagal mengambil data siswa"))
        }
    }

    override suspend fun getStudentDetail(id: String): ApiResult<StudentDto> {
        return try {
            val result = api?.getStudentDetail(id) ?: ApiResult.Error(ApiError.NotFound)
            if (result is ApiResult.Success) cache.cacheStudent(result.data)
            result
        } catch (e: Exception) {
            val cached = cache.getStudentById(id)
            if (cached != null) ApiResult.Success(cached)
            else if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummyStudentById(id))
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil detail siswa"))
        }
    }

    private fun fallbackGetStudents(error: ApiError): ApiResult<List<StudentDto>> {
        val cached = cache.getStudents()
        if (cached.isNotEmpty()) return ApiResult.Success(cached)
        if (DevMode.isEnabled) return ApiResult.Success(DummyDataProvider.dummyStudents)
        return ApiResult.Error(error)
    }
}
