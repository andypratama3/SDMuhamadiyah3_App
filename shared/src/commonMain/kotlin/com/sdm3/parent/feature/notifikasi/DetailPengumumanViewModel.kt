package com.sdm3.parent.feature.notifikasi

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.ArticleAttachmentDto
import com.sdm3.parent.domain.repository.ArticleRepositoryContract

data class DetailPengumumanUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val announcementId: String = "",
    val title: String = "",
    val content: String = "",
    val excerpt: String = "",
    val author: String = "",
    val date: String = "",
    val imageUrl: String? = null,
    val attachments: List<ArticleAttachmentDto> = emptyList()
) : ScreenState

class DetailPengumumanViewModel(
    private val articleRepository: ArticleRepositoryContract
) : BaseViewModel<DetailPengumumanUiState>(DetailPengumumanUiState()) {

    fun loadDetail(announcementId: String) {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null, isEmpty = false, announcementId = announcementId) }
            when (val result = articleRepository.getArticleById(announcementId)) {
                is ApiResult.Success -> {
                    val article = result.data
                    updateState {
                        it.copy(
                            isLoading = false,
                            title = article.title,
                            excerpt = article.excerpt.orEmpty(),
                            content = article.content.orEmpty(),
                            author = article.authorName ?: article.category.orEmpty(),
                            date = article.publishedAt ?: article.createdAt.orEmpty(),
                            imageUrl = article.image,
                            attachments = article.attachments.orEmpty()
                        )
                    }
                }
                is ApiResult.Error -> {
                    updateState {
                        it.copy(isLoading = false, errorMessage = result.error.toUserMessage())
                    }
                }
            }
        }
    }

    fun refresh() {
        val s = uiState.value
        loadDetail(s.announcementId)
    }
}
