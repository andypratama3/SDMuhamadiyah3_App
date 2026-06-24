package com.sdm3.parent.domain.repository

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.StudentDto

interface StudentRepositoryContract {
    suspend fun getStudents(): ApiResult<List<StudentDto>>
    suspend fun getStudentDetail(id: String): ApiResult<StudentDto>
}
