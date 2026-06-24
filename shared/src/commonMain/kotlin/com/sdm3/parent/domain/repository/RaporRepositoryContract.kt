package com.sdm3.parent.domain.repository

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.RaporInstanceDto
import com.sdm3.parent.data.remote.dto.RaporVerifyResponse

interface RaporRepositoryContract {
    suspend fun getRaporInstances(studentId: String): ApiResult<List<RaporInstanceDto>>
    suspend fun getDownloadUrl(id: String): ApiResult<String>
    suspend fun verifyQr(qrData: String): ApiResult<RaporVerifyResponse>
}
