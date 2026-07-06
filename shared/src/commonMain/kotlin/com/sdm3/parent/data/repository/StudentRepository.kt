package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.safeApiCallWithCache
import com.sdm3.parent.data.remote.api.StudentApi
import com.sdm3.parent.data.remote.dto.StudentDto
import com.sdm3.parent.domain.repository.StudentRepositoryContract

open class StudentRepository(
    private val api: StudentApi,
    private val cache: CacheDataSource,
) : StudentRepositoryContract {

    override suspend fun getStudents(): ApiResult<List<StudentDto>> =
        safeApiCallWithCache(
            fallback = "Gagal mengambil data siswa",
            block = {
                when (val result = api.getStudents()) {
                    is ApiResult.Success -> {
                        cache.cacheStudents(result.data)
                        result
                    }
                    is ApiResult.Error -> result
                }
            },
            cacheFallback = { cache.getStudents().takeIf { it.isNotEmpty() } },
        )

    override suspend fun getStudentDetail(id: String): ApiResult<StudentDto> =
        safeApiCallWithCache(
            fallback = "Gagal mengambil detail siswa",
            block = {
                when (val result = api.getStudentDetail(id)) {
                    is ApiResult.Success -> {
                        cache.cacheStudent(result.data)
                        result
                    }
                    is ApiResult.Error -> result
                }
            },
            cacheFallback = { cache.getStudentById(id) },
        )
}
