package com.colab.myfriend.repository

import com.colab.myfriend.app.DataProduct
import kotlinx.coroutines.flow.Flow

interface DataProductsRepo {

    fun getProducts(keyword: String): Flow<List<DataProduct>>

    fun searchProducts(keyword: String): Flow<List<DataProduct>>
}