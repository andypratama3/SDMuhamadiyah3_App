package com.sdm3.parent.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDto(
    val id: String,
    val title: String,
    val excerpt: String? = null,
    val content: String? = null,
    val photo: String? = null,
    val image: String? = null,
    val slug: String? = null,
    @SerialName("author_name")
    val authorName: String? = null,
    val category: String? = null,
    val categories: List<ArticleCategoryDto>? = null,
    val attachments: List<ArticleAttachmentDto>? = null,
    @SerialName("published_at")
    val publishedAt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class ArticleAttachmentDto(
    val name: String,
    val url: String,
    val size: String? = null
)

@Serializable
data class ArticleCategoryDto(
    val id: String,
    val name: String
)
