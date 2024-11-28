package com.colab.myfriend

import androidx.room.Query
import com.colab.myfriend.database.Friend
import com.crocodic.core.data.CoreDao
import kotlinx.coroutines.flow.Flow

interface FriendRepository : CoreDao<Friend> {
    suspend fun getAllFriends(): Flow<List<Friend>>
    fun getFriendById(id: Int): Flow<Friend?>
    suspend fun searchFriend(keyword: String?): Flow<List<Friend>>
}
