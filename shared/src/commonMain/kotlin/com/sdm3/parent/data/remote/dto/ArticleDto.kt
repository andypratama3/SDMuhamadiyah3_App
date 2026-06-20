package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDto(
    val id: String,
    val title: String,
    val content: String? = null,
    val image: String? = null,
    val category: String? = null,
    @SerialName("published_at")
    val publishedAt: String? = null
)
