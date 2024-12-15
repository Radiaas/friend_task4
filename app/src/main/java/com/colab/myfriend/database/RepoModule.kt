package com.colab.myfriend.database

import com.colab.myfriend.repository.DataProductsRepo
import com.colab.myfriend.repository.ImplDataProductRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepoModule {


    @Singleton
    @Binds
    abstract fun bindDataProductRepo(
        implDataProductRepo: ImplDataProductRepo
    ): DataProductsRepo

}