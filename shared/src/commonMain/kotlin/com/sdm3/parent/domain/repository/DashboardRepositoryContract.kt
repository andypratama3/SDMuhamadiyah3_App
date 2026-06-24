package com.sdm3.parent.domain.repository

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.DashboardDto

interface DashboardRepositoryContract {
    suspend fun getDashboard(studentId: String): ApiResult<DashboardDto>
}
