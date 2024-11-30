package com.colab.myfriend

import android.content.Context
import com.colab.myfriend.repository.FriendRepository
import com.colab.myfriend.repository.FriendRepositoryImpl
import com.colab.myfriend.adapter.FriendDao
import com.colab.myfriend.database.MyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMyDatabase(@ApplicationContext context: Context): MyDatabase {
        return MyDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideFriendDao(myDatabase: MyDatabase): FriendDao {
        return myDatabase.friendDao()
    }

    @Provides
    @Singleton
    fun provideFriendRepository(
        friendDao: FriendDao
    ): FriendRepository {
        return FriendRepositoryImpl(friendDao)
    }
}


