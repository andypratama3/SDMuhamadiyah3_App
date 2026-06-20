package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.DashboardApi
import com.sdm3.parent.data.remote.dto.DashboardDto

class DashboardRepository(private val api: DashboardApi) {

    suspend fun getDashboard(studentId: String): ApiResult<DashboardDto> {
        return try {
            api.getDashboard(studentId)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data dashboard"))
        }
    }
}
