package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.di.DevMode
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.dummy.DummyDataProvider
import com.sdm3.parent.data.remote.api.RaporApi
import com.sdm3.parent.data.remote.dto.RaporInstanceDto
import com.sdm3.parent.data.remote.dto.RaporVerifyResponse
import com.sdm3.parent.domain.repository.RaporRepositoryContract

class RaporRepository(
    private val api: RaporApi,
    private val cache: CacheDataSource,
) : RaporRepositoryContract {

    override suspend fun getRaporInstances(studentId: String): ApiResult<List<RaporInstanceDto>> {
        return try {
            val result = api.getRaporInstances(studentId)
            if (result is ApiResult.Success) cache.cacheRaporInstances(studentId, result.data)
            result
        } catch (e: Exception) {
            val cached = cache.getRaporInstances(studentId)
            if (cached.isNotEmpty()) ApiResult.Success(cached)
            else if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummyRaporInstances)
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil data rapor"))
        }
    }

    override suspend fun getDownloadUrl(id: String): ApiResult<String> {
        return try {
            api.getDownloadUrl(id)
        } catch (e: Exception) {
            if (DevMode.isEnabled) ApiResult.Success("https://admin.sdm3.sch.id/storage/rapor/$id")
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mendapatkan URL unduhan"))
        }
    }

    override suspend fun verifyQr(qrData: String): ApiResult<RaporVerifyResponse> {
        return try {
            api.verifyQr(qrData)
        } catch (e: Exception) {
            if (DevMode.isEnabled) ApiResult.Success(DummyDataProvider.dummyRaporVerifyResponse)
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal memverifikasi QR"))
        }
    }
}
