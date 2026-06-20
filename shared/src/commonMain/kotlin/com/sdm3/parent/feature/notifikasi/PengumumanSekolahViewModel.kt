package com.sdm3.parent.feature.notifikasi

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.core.network.ApiResult
import com.sdm3.parent.data.remote.dto.ArticleDto
import com.sdm3.parent.data.repository.ArticleRepository

data class PengumumanSekolahUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val articles: List<ArticleDto> = emptyList()
) : ScreenState

class PengumumanSekolahViewModel(
    private val articleRepository: ArticleRepository
) : BaseViewModel<PengumumanSekolahUiState>(PengumumanSekolahUiState()) {

    fun loadArticles() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        launchSafely {
            when (val result = articleRepository.getArticles()) {
                is ApiResult.Success -> {
                    updateState {
                        it.copy(
                            articles = result.data,
                            isLoading = false,
                            isEmpty = result.data.isEmpty()
                        )
                    }
                }
                is ApiResult.Error -> {
                    updateState { it.copy(isLoading = false, errorMessage = result.error.toUserMessage()) }
                }
            }
        }
    }

    fun refresh() {
        loadArticles()
    }
}
