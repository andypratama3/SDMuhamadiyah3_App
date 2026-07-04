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
                            excerpt = cleanHtml(article.excerpt.orEmpty()),
                            content = cleanHtml(article.content.orEmpty()),
                            author = article.authorName ?: article.category.orEmpty(),
                            date = formatArticleDate(article.publishedAt ?: article.createdAt.orEmpty()),
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

    private fun cleanHtml(raw: String): String {
        if (raw.isBlank()) return raw
        var s = raw
        s = s.replace(Regex("(?i)<br\\s*/?>"), "\n")
        s = s.replace(Regex("(?i)</p>"), "\n\n")
        s = s.replace(Regex("(?i)</div>"), "\n")
        s = s.replace(Regex("(?i)<li[^>]*>"), "• ")
        s = s.replace(Regex("(?i)</li>"), "\n")
        s = s.replace(Regex("<[^>]+>"), "")
        s = s.replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&rsquo;", "'")
            .replace("&lsquo;", "'")
            .replace("&ldquo;", "\"")
            .replace("&rdquo;", "\"")
        s = s.replace(Regex("[ \\t]+"), " ")
        s = s.replace(Regex("\\n{3,}"), "\n\n")
        return s.trim()
    }

    private val bulanArtikel = listOf(
        "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
    )

    private fun formatArticleDate(iso: String): String {
        if (iso.isBlank()) return iso
        val datePart = iso.substringBefore('T').substringBefore(' ')
        val parts = datePart.split('-')
        if (parts.size < 3) return iso
        val year = parts[0].toIntOrNull() ?: return iso
        val month = parts[1].toIntOrNull() ?: return iso
        val day = parts[2].take(2).toIntOrNull() ?: return iso
        if (month !in 1..12) return iso
        return "$day ${bulanArtikel[month - 1]} $year"
    }
}
