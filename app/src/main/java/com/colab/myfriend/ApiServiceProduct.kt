package com.colab.myfriend

import com.crocodic.core.api.ModelResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServiceProduct {

    @GET("products/search")
    suspend fun getProducts(
        @Query("q") keyword: String
    ) : ResponseDataProduct
}