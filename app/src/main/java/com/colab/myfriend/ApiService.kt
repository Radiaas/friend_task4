package com.colab.myfriend

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(
        @Body credentials: Map<String, String>
    ): Response<User>
}
