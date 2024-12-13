package com.colab.myfriend.adapter


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import com.colab.myfriend.database.NewsArticle
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    // Mendapatkan semua berita, diurutkan berdasarkan waktu publikasi
    @Query("SELECT * FROM news ORDER BY publishedAt DESC")
    fun getAllNews(): Flow<List<NewsArticle>>

    // Mendapatkan berita berdasarkan ID
    @Query("SELECT * FROM news WHERE id = :id LIMIT 1")
    fun getNewsById(id: Int): Flow<NewsArticle?>

    // Menyisipkan daftar berita
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<NewsArticle>)

    @Query("SELECT * FROM news WHERE title LIKE :keyword OR description LIKE :keyword ORDER BY publishedAt DESC")
    fun searchNews(keyword: String): Flow<List<NewsArticle>>

    // Menghapus berita tertentu
    @Delete
    suspend fun deleteNews(news: NewsArticle)

    // Menghapus semua berita
    @Query("DELETE FROM news")
    suspend fun deleteAllNews()
}
