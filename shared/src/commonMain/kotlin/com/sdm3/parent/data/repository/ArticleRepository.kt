package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.safeApiCall
import com.sdm3.parent.core.network.safeApiCallWithCache
import com.sdm3.parent.data.remote.api.ArticleApi
import com.sdm3.parent.data.remote.dto.ArticleDto
import com.sdm3.parent.domain.repository.ArticleRepositoryContract

class ArticleRepository(
    private val api: ArticleApi,
    private val cache: CacheDataSource,
) : ArticleRepositoryContract {

    override suspend fun getArticles(): ApiResult<List<ArticleDto>> =
        safeApiCallWithCache(
            fallback = "Gagal mengambil artikel",
            block = {
                when (val result = api.getArticles()) {
                    is ApiResult.Success -> {
                        cache.cacheArticles(result.data)
                        result
                    }
                    is ApiResult.Error -> result
                }
            },
            cacheFallback = {
                cache.getArticles().takeIf { it.isNotEmpty() }
            },
        )

    override suspend fun getArticleById(id: String): ApiResult<ArticleDto> =
        safeApiCallWithCache(
            fallback = "Gagal mengambil artikel",
            block = { api.getArticleById(id) },
            cacheFallback = { cache.getArticles().firstOrNull { it.id == id } },
        )

    override suspend fun getArticleBySlug(slug: String): ApiResult<ArticleDto> =
        safeApiCall("Gagal mengambil artikel") { api.getArticleBySlug(slug) }
}
