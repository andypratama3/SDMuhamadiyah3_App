package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.StudentApi
import com.sdm3.parent.data.remote.dto.StudentDto
import com.sdm3.parent.domain.repository.StudentRepositoryContract

open class StudentRepository(
    private val api: StudentApi,
    private val cache: CacheDataSource,
) : StudentRepositoryContract {

    override suspend fun getStudents(): ApiResult<List<StudentDto>> {
        return try {
            when (val result = api.getStudents()) {
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
            val result = api.getStudentDetail(id)
            if (result is ApiResult.Success) cache.cacheStudent(result.data)
            result
        } catch (e: Exception) {
            val cached = cache.getStudentById(id)
            if (cached != null) ApiResult.Success(cached)
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil detail siswa"))
        }
    }

    private fun fallbackGetStudents(error: ApiError): ApiResult<List<StudentDto>> {
        val cached = cache.getStudents()
        if (cached.isNotEmpty()) return ApiResult.Success(cached)
        return ApiResult.Error(error)
    }
}
