package com.colab.myfriend

import android.util.Log
import com.crocodic.core.api.ApiObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ImplDataProductRepo @Inject constructor(private val apiServiceProduct: ApiServiceProduct) : DataProductsRepo {

    override fun getProducts(keyword: String): Flow<List<DataProduct>> = flow {
        Log.d("ImplDataProductRepo", "Fetching products with keyword: $keyword")
        ApiObserver.run(
            { apiServiceProduct.getProducts(keyword) },
            false,
            object : ApiObserver.ModelResponseListener<ResponseDataProduct> {
                override suspend fun onSuccess(response: ResponseDataProduct) {
                    Log.d("ImplDataProductRepo", "Success: ${response.products}")
                    emit(response.products)
                }
                override suspend fun onError(response: ResponseDataProduct) {
                    Log.e("ImplDataProductRepo", "Error: ${response}")
                    emit(emptyList())
                }
            }
        )

    }


}