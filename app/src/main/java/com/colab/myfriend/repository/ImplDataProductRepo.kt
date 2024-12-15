package com.colab.myfriend.repository

import com.colab.myfriend.ApiServiceProduct
import com.colab.myfriend.Api.ResponseDataProduct
import com.colab.myfriend.app.DataProduct
import com.crocodic.core.api.ApiObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ImplDataProductRepo @Inject constructor(private val apiServiceProduct: ApiServiceProduct) :
    DataProductsRepo {

    override fun getProducts(keyword: String): Flow<List<DataProduct>> = flow {
        ApiObserver.run(
            { apiServiceProduct.getProducts(keyword) },
            false,
            object : ApiObserver.ModelResponseListener<ResponseDataProduct> {
                override suspend fun onSuccess(response: ResponseDataProduct) {
                    emit(response.products)
                }

                override suspend fun onError(response: ResponseDataProduct) {
                    emit(emptyList())
                }
            })
    }


    override fun searchProducts(keyword: String): Flow<List<DataProduct>> = flow {
        ApiObserver.run(
            { apiServiceProduct.getProducts(keyword) }, // Perbaikan disini
            false,
            object : ApiObserver.ModelResponseListener<ResponseDataProduct> {
                override suspend fun onSuccess(response: ResponseDataProduct) {
                    emit(response.products)
                }

                override suspend fun onError(response: ResponseDataProduct) {
                    emit(emptyList())
                }
            })
    }


}
