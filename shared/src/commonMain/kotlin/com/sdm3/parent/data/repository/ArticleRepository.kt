package com.sdm3.parent.data.repository

import com.sdm3.parent.cache.CacheDataSource
import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.ArticleApi
import com.sdm3.parent.data.remote.dto.ArticleDto
import com.sdm3.parent.domain.repository.ArticleRepositoryContract

class ArticleRepository(
    private val api: ArticleApi,
    private val cache: CacheDataSource,
) : ArticleRepositoryContract {

    override suspend fun getArticles(): ApiResult<List<ArticleDto>> {
        return try {
            val result = api.getArticles()
            if (result is ApiResult.Success) cache.cacheArticles(result.data)
            result
        } catch (e: Exception) {
            val cached = cache.getArticles()
            if (cached.isNotEmpty()) ApiResult.Success(cached)
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil artikel"))
        }
    }

    override suspend fun getArticleById(id: String): ApiResult<ArticleDto> {
        return try {
            api.getArticleById(id)
        } catch (e: Exception) {
            val cached = cache.getArticles().firstOrNull { it.id == id }
            if (cached != null) ApiResult.Success(cached)
            else ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil artikel"))
        }
    }

    override suspend fun getArticleBySlug(slug: String): ApiResult<ArticleDto> {
        return try {
            api.getArticleBySlug(slug)
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil artikel"))
        }
    }
}
