package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.ArticleApi
import com.sdm3.parent.data.remote.dto.ArticleDto
import com.sdm3.parent.domain.repository.ArticleRepositoryContract

class ArticleRepository(private val api: ArticleApi) : ArticleRepositoryContract {

    override suspend fun getArticles(): ApiResult<List<ArticleDto>> {
        return try {
            api.getArticles()
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil artikel"))
        }
    }
}
