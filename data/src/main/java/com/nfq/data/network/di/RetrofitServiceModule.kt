package com.nfq.data.network.di

import com.nfq.data.remote.service.NFQSummitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object RetrofitServiceModule {

    @Provides
    @Singleton
    fun provideNFQSummitService(retrofit: Retrofit): NFQSummitService {
        return retrofit.create(NFQSummitService::class.java)
    }
}
