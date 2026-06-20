package com.sdm3.parent.data.repository

import com.sdm3.parent.core.network.ApiError
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.api.ArticleApi
import com.sdm3.parent.data.remote.dto.ArticleDto

class ArticleRepository(private val api: ArticleApi) {

    suspend fun getArticles(): ApiResult<List<ArticleDto>> {
        return try {
            api.getArticles()
        } catch (e: Exception) {
            ApiResult.Error(ApiError.Unknown(e.message ?: "Gagal mengambil artikel"))
        }
    }
}
