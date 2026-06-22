package com.sdm3.parent.feature.notifikasi

import com.sdm3.parent.core.base.BaseViewModel
import com.sdm3.parent.core.base.ScreenState
import com.sdm3.parent.data.remote.dto.ArticleDto
import kotlinx.coroutines.delay

data class PengumumanSekolahUiState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    override val isEmpty: Boolean = false,
    val articles: List<ArticleDto> = emptyList()
) : ScreenState

class PengumumanSekolahViewModel : BaseViewModel<PengumumanSekolahUiState>(PengumumanSekolahUiState()) {

    fun loadArticles() {
        launchSafely {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            delay(1000)
            val dummyArticles = listOf(
                ArticleDto(id = "1", title = "Jadwal Ujian Akhir Semester Ganjil 2023/2024", category = "Akademik", publishedAt = "12 Des 2023"),
                ArticleDto(id = "2", title = "Lomba Mewarnai Tingkat Sekolah Dasar", category = "Kegiatan", publishedAt = "15 Des 2023"),
                ArticleDto(id = "3", title = "Pembaruan Kurikulum Merdeka Belajar", category = "Akademik", publishedAt = "10 Des 2023"),
                ArticleDto(id = "4", title = "Penggalangan Dana Bencana Alam", category = "Umum", publishedAt = "08 Des 2023")
            )
            updateState {
                it.copy(
                    articles = dummyArticles,
                    isLoading = false,
                    isEmpty = dummyArticles.isEmpty()
                )
            }
        }
    }

    fun refresh() {
        loadArticles()
    }
}
