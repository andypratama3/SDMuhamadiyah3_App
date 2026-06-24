package com.sdm3.parent.domain.repository

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.ExtracurricularDto

interface ExtracurricularRepositoryContract {
    suspend fun getExtracurriculars(studentId: String): ApiResult<List<ExtracurricularDto>>
}
