package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.RaporApi
import com.sdm3.parent.data.remote.dto.RaporInstanceDto
import com.sdm3.parent.data.remote.dto.RaporVerifyResponse

class RaporRepository(private val api: RaporApi) {

    suspend fun getRaporInstances(studentId: String): ApiResult<List<RaporInstanceDto>> {
        return try {
            api.getRaporInstances(studentId)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data rapor"))
        }
    }

    suspend fun getDownloadUrl(id: String): ApiResult<String> {
        return try {
            api.getDownloadUrl(id)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mendapatkan URL unduhan"))
        }
    }

    suspend fun verifyQr(qrData: String): ApiResult<RaporVerifyResponse> {
        return try {
            api.verifyQr(qrData)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal memverifikasi QR"))
        }
    }
}
