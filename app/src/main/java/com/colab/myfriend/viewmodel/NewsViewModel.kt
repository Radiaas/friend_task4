package com.colab.myfriend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colab.myfriend.database.NewsArticle
import com.colab.myfriend.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    // StateFlow for loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // StateFlow for error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun getAllNews(): Flow<List<NewsArticle>> {
        return repository.getAllNews()
    }

    fun searchNews(keyword: String): Flow<List<NewsArticle>> {
        return repository.searchNewsByKeyword(keyword)
    }


    fun getNewsById(id: Int): Flow<NewsArticle?> {
        return repository.getNewsById(id)
    }

    fun refreshNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val news = repository.fetchNewsFromApi()
                repository.clearNews()
                repository.saveNews(news) // Simpan berita ke database
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteNews(newsArticle: NewsArticle) {
        viewModelScope.launch {
            repository.deleteNews(newsArticle)
        }
    }

    fun clearAllNews() {
        viewModelScope.launch {
            repository.clearNews()
        }
    }
}
