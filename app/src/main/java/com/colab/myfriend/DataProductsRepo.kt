package com.colab.myfriend

import kotlinx.coroutines.flow.Flow

interface DataProductsRepo {
    fun getProducts(keyword: String): Flow<List<DataProduct>>
}