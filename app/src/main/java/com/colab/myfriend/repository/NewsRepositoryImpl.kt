package com.colab.myfriend.repository

import com.colab.myfriend.adapter.NewsDao
import com.colab.myfriend.ApiService
import com.colab.myfriend.MediaStackApi
import com.colab.myfriend.MediaStackArticle
import com.colab.myfriend.database.NewsArticle
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao,
    private val apiService: ApiService,
    @MediaStackApi private val mediaStackApiService: ApiService // Gunakan MediaStack API
) : NewsRepository {

    override fun getAllNews(): Flow<List<NewsArticle>> = newsDao.getAllNews()

    override suspend fun fetchNewsFromApi(): List<NewsArticle> {
        return TODO("Provide the return value")
    }

    override fun searchNewsByKeyword(keyword: String): Flow<List<NewsArticle>> = newsDao.searchNews("%$keyword%")

    override suspend fun clearNews() = newsDao.deleteAllNews()

    override suspend fun saveNews(news: List<NewsArticle>) = newsDao.insertNews(news)

    override suspend fun fetchMediaStackNews(keywords: String?): List<MediaStackArticle> {
        return try {
            val response = mediaStackApiService.getMediaStackNews(
                accessKey = "YOUR_ACCESS_KEY", // Pastikan API key diambil dari konfigurasi yang aman
                keywords = keywords
            )
            if (response.isSuccessful && response.body() != null) {
                response.body()?.data ?: emptyList()
            } else {
                Timber.e("MediaStack API failed: ${response.code()} - ${response.message()} - ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching news from MediaStack API")
            emptyList()
        }
    }

    override fun getNewsById(id: Int): Flow<NewsArticle?> = newsDao.getNewsById(id)

    override suspend fun deleteNews(news: NewsArticle) = newsDao.deleteNews(news)
}
