package com.sdm3.parent.data.remote.api

import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.core.network.HttpClientProvider
import com.sdm3.parent.core.network.toApiResult
import com.sdm3.parent.data.remote.dto.ArticleDto
import io.ktor.client.request.get
import io.ktor.client.request.url

class ArticleApi(private val provider: HttpClientProvider) {

    suspend fun getArticles(): ApiResult<List<ArticleDto>> {
        val response = provider.client.get {
            url(Endpoints.PARENT_ARTICLES)
            provider.applyAuthHeader(this)
        }
        provider.handleSessionExpiredIfNeeded(response)
        return response.toApiResult()
    }
}
