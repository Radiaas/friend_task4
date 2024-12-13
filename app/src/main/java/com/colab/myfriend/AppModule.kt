package com.colab.myfriend

import android.content.Context
import com.colab.myfriend.adapter.NewsDao
import com.colab.myfriend.database.NewsDatabase
import com.colab.myfriend.repository.NewsRepository
import com.colab.myfriend.repository.NewsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import javax.inject.Qualifier

// Qualifiers for distinguishing between API services
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CustomApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MediaStackApi

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Provide News Database and DAO
    @Provides
    @Singleton
    fun provideNewsDatabase(@ApplicationContext context: Context): NewsDatabase {
        return NewsDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideNewsDao(newsDatabase: NewsDatabase): NewsDao {
        return newsDatabase.newsDao()
    }

    // Provide User Database and DAO
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

    // AppModule.kt
    @Provides
    @Singleton
    fun provideNewsRepository(
        newsDao: NewsDao,
        @CustomApi apiService: ApiService,
        @MediaStackApi mediaStackApiService: ApiService // Tambahkan parameter ini
    ): NewsRepository {
        return NewsRepositoryImpl(
            newsDao = newsDao,
            apiService = apiService,
            mediaStackApiService = mediaStackApiService // Inject ke repository
        )
    }

}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @CustomApi
    @Provides
    @Singleton
    fun provideRetrofitForCustomAPI(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://neptune74.crocodic.net/myfriend-kelasindustri/public/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @MediaStackApi
    @Provides
    @Singleton
    fun provideRetrofitForMediaStack(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://api.mediastack.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @CustomApi
    @Provides
    @Singleton
    fun provideApiServiceForCustomAPI(@CustomApi retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @MediaStackApi
    @Provides
    @Singleton
    fun provideApiServiceForMediaStack(@MediaStackApi retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

}
