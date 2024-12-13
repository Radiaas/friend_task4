package com.colab.myfriend

import com.colab.myfriend.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("login")
    suspend fun login(
        @Body credentials: Map<String, String>
    ): Response<User>

    @GET("v1/news")
    suspend fun getMediaStackNews(
        @Query("access_key") accessKey: String,
        @Query("keywords") keywords: String? = null,
        @Query("languages") languages: String = "en" // Default ke bahasa Inggris
    ): Response<MediaStackResponse>

}
