package com.colab.myfriend.adapter

import androidx.room.Dao
import androidx.room.Query
import com.colab.myfriend.database.Friend
import com.crocodic.core.data.CoreDao
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao : CoreDao<Friend> {

    @Query("SELECT * FROM friend WHERE id = :id")
    fun getItemById(id: Int): Flow<Friend?>

//    @Query("SELECT * FROM friend WHERE name LIKE :keyword")
//    fun findFriend(keyword: String): List<Friend>

    @Query("SELECT * FROM friend")
    fun getAll(): Flow<List<Friend>>

    @Query("SELECT * FROM friend WHERE name LIKE :keyword OR school LIKE :keyword")
    fun searchFriends(keyword: String): Flow<List<Friend>>

}
