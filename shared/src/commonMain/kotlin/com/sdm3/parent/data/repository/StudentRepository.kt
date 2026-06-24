package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.StudentApi
import com.sdm3.parent.data.remote.dto.StudentDto
import com.sdm3.parent.domain.repository.StudentRepositoryContract

open class StudentRepository(private val api: StudentApi?) : StudentRepositoryContract {

    override suspend fun getStudents(): ApiResult<List<StudentDto>> {
        return try {
            api?.getStudents() ?: ApiResult.Success(emptyList())
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data siswa"))
        }
    }

    override suspend fun getStudentDetail(id: String): ApiResult<StudentDto> {
        return try {
            api?.getStudentDetail(id) ?: ApiResult.Error(ApiError.NotFound)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil detail siswa"))
        }
    }
}
