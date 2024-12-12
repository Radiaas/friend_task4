package com.colab.myfriend

import android.content.Context
import com.colab.myfriend.adapter.FriendDao
import com.colab.myfriend.database.MyDatabase
import com.colab.myfriend.repository.FriendRepository
import com.colab.myfriend.repository.FriendRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase {
        return UserDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(userDatabase: UserDatabase): UserDao {
        return userDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideFriendRepository(
        friendDao: FriendDao
    ): FriendRepository {
        return FriendRepositoryImpl(friendDao)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://neptune74.crocodic.net/myfriend-kelasindustri/public/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
