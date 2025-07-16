package com.kinship.mobile.app.di

import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.data.source.remote.repository.ApiRepositoryImpl
import com.kinship.mobile.app.data.source.remote.repository.ApiServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun getApiRepositoryImpl(apiServices: ApiServices): ApiRepository {
        return ApiRepositoryImpl(apiServices)
    }

}
