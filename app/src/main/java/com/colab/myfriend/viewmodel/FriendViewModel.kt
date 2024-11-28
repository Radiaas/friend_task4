package com.colab.myfriend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colab.myfriend.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.colab.myfriend.adapter.FriendDao
import com.colab.myfriend.database.Friend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

// Annotate the ViewModel with @HiltViewModel
@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendDao: FriendDao,
    private val repository: FriendRepository
) : ViewModel() {

    fun getFriend() = friendDao.getAll()

    fun getFriendById(id: Int) = friendDao.getItemById(id)

    // This function is suspend and must be called from a coroutine
    suspend fun insertFriend(data: Friend) {
        friendDao.insert(data)
    }

    // This function is suspend and must be called from a coroutine
    suspend fun editFriend(data: Friend) {
        friendDao.update(data)
    }

    // This function is suspend and must be called from a coroutine
    suspend fun deleteFriend(data: Friend) {
        friendDao.delete(data)
    }

    suspend fun deleteFriendById(id: Int) {
        val friend = friendDao.getItemById(id).firstOrNull()
        friend?.let {
            friendDao.delete(it)
        }
    }

    suspend fun searchFriend(keyword: String): Flow<List<Friend>> {
        return repository.searchFriend(keyword)
    }

    fun updateFriend(friend: Friend) {
        viewModelScope.launch {
            repository.update(friend) // Langsung gunakan repository
        }
    }


}
