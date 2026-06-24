package com.sdm3.parent.domain.repository

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.ArticleDto

interface ArticleRepositoryContract {
    suspend fun getArticles(): ApiResult<List<ArticleDto>>
}
