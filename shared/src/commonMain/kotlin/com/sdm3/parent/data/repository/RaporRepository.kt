package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.safeApiCall
import com.sdm3.parent.core.network.safeApiCallWithCache
import com.sdm3.parent.data.remote.api.RaporApi
import com.sdm3.parent.data.remote.dto.RaporInstanceDto
import com.sdm3.parent.data.remote.dto.RaporVerifyResponse
import com.sdm3.parent.domain.repository.RaporRepositoryContract

class RaporRepository(
    private val api: RaporApi,
    private val cache: CacheDataSource,
) : RaporRepositoryContract {

    override suspend fun getRaporInstances(studentId: String): ApiResult<List<RaporInstanceDto>> =
        safeApiCallWithCache(
            fallback = "Gagal mengambil data rapor",
            block = {
                when (val result = api.getRaporInstances(studentId)) {
                    is ApiResult.Success -> {
                        cache.cacheRaporInstances(studentId, result.data)
                        result
                    }
                    is ApiResult.Error -> result
                }
            },
            cacheFallback = { cache.getRaporInstances(studentId).takeIf { it.isNotEmpty() } },
        )

    override suspend fun getDownloadUrl(id: String): ApiResult<String> =
        safeApiCall("Gagal mendapatkan URL unduhan") { api.getDownloadUrl(id) }

    override suspend fun verifyQr(qrData: String): ApiResult<RaporVerifyResponse> =
        safeApiCall("Gagal memverifikasi QR") { api.verifyQr(qrData) }
}
