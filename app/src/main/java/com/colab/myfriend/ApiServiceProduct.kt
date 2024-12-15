package com.colab.myfriend

import com.colab.myfriend.Api.ResponseDataProduct
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServiceProduct {

    @GET("products/search")
    suspend fun getProducts(
        @Query("q") keyword: String
    ) : ResponseDataProduct
}