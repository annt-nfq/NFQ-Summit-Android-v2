package com.nfq.nfqsummit.di

import com.nfq.data.local.EventLocal
import com.nfq.data.local.EventLocalImpl
import com.nfq.data.remote.AttractionRemote
import com.nfq.data.remote.AttractionRemoteImpl
import com.nfq.data.remote.BlogRemote
import com.nfq.data.remote.BlogRemoteImpl
import com.nfq.data.remote.EventRemote
import com.nfq.data.remote.EventRemoteImpl
import com.nfq.data.remote.TranslationRemote
import com.nfq.data.remote.TranslationRemoteImpl
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

    @Binds
    @Singleton
    abstract fun bindBlogRemote(
        blogRemoteImpl: BlogRemoteImpl
    ): BlogRemote

    @Binds
    @Singleton
    abstract fun bingAttractionRemote(
        attractionRemoteImpl: AttractionRemoteImpl
    ): AttractionRemote

    @Binds
    @Singleton
    abstract fun bindTranslationRemote(
        translationRemoteImpl: TranslationRemoteImpl
    ): TranslationRemote
}