package com.colab.myfriend.viewmodel

import androidx.lifecycle.viewModelScope
import com.colab.myfriend.app.DataProduct
import com.colab.myfriend.repository.DataProductsRepo
import com.colab.myfriend.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.colab.myfriend.adapter.FriendDao
import com.colab.myfriend.database.Friend
import com.crocodic.core.base.viewmodel.CoreViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendDao: FriendDao,
    private val dataProductsRepo: DataProductsRepo,
    private val repository: FriendRepository
) : CoreViewModel() {

    private val _product = MutableStateFlow<List<DataProduct>>(emptyList())
    val product: StateFlow<List<DataProduct>> = _product


    fun getProduct(keyword: String = "") = viewModelScope.launch {
        dataProductsRepo.getProducts(keyword).collect { it: List<DataProduct> ->
            _product.emit(it)
        }
    }

    fun searchProduct(keyword: String): Flow<List<DataProduct>> {
        return dataProductsRepo.getProducts(keyword)
    }


    fun getFriend() = friendDao.getAll()

    fun getFriendById(id: Int) = friendDao.getItemById(id)

    suspend fun insertFriend(data: Friend) {
        friendDao.insert(data)
    }

    suspend fun editFriend(data: Friend) {
        friendDao.update(data)
    }

    suspend fun deleteFriend(data: Friend) {
        friendDao.delete(data)
    }

    suspend fun searchFriend(keyword: String): Flow<List<Friend>> {
        return repository.searchFriend(keyword)
    }

    override fun apiLogout() {
        TODO("Not yet implemented")
    }

    override fun apiRenewToken() {
        TODO("Not yet implemented")
    }

}