package com.colab.myfriend.repository

import com.colab.myfriend.MediaStackArticle
import com.colab.myfriend.database.NewsArticle
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    /**
     * Fetches all news articles from the local database as a flow.
     */
    fun getAllNews(): Flow<List<NewsArticle>>

    /**
     * Fetches the latest news articles from an API.
     * @return A list of [NewsArticle].
     */
    suspend fun fetchNewsFromApi(): List<NewsArticle>

    /**
     * Clears all news articles from the local database.
     */
    suspend fun clearNews()

    /**
     * Searches news articles by a given keyword.
     * @param keyword The keyword to search for.
     * @return A flow of list of [NewsArticle].
     */
    fun searchNewsByKeyword(keyword: String): Flow<List<NewsArticle>>

    /**
     * Saves a list of news articles to the local database.
     * @param news List of [NewsArticle] to save.
     */
    suspend fun saveNews(news: List<NewsArticle>)

    /**
     * Fetches news articles from MediaStack API.
     * @param keywords Keywords to search for news articles.
     * @return A list of [MediaStackArticle].
     */
    suspend fun fetchMediaStackNews(keywords: String? = null): List<MediaStackArticle>

    /**
     * Retrieves a news article by its ID from the local database.
     * @param id The ID of the news article.
     * @return A [NewsArticle] or null if not found.
     */
    fun getNewsById(id: Int): Flow<NewsArticle?>

    /**
     * Deletes a specific news article from the local database.
     * @param news The [NewsArticle] to delete.
     */
    suspend fun deleteNews(news: NewsArticle)
}
