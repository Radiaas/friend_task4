package com.colab.myfriend

data class MediaStackResponse(
    val pagination: Pagination,
    val data: List<MediaStackArticle>
)

data class Pagination(
    val limit: Int,
    val offset: Int,
    val count: Int,
    val total: Int
)

data class MediaStackArticle(
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val image: String?,
    val published_at: String?
)
