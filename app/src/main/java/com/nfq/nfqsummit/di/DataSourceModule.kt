package com.nfq.nfqsummit.di

import com.nfq.data.local.EventLocal
import com.nfq.data.local.EventLocalImpl
import com.nfq.data.remote.EventRemote
import com.nfq.data.remote.EventRemoteImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun bindEventRemote(
        eventRemoteImpl: EventRemoteImpl
    ): EventRemote

    @Binds
    @Singleton
    abstract fun bindEventLocal(
        eventLocalImpl: EventLocalImpl
    ): EventLocal
}