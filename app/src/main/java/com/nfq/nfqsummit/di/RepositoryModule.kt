package com.nfq.nfqsummit.di

import com.nfq.data.domain.repository.ExploreRepository
import com.nfq.data.domain.repository.BlogRepository
import com.nfq.data.domain.repository.EventRepository
import com.nfq.data.domain.repository.NFQSummitRepository
import com.nfq.data.domain.repository.TranslationRepository
import com.nfq.data.remote.repository.ExploreRepositoryImpl
import com.nfq.data.remote.repository.BlogRepositoryImpl
import com.nfq.data.remote.repository.EventRepositoryImpl
import com.nfq.data.remote.repository.NFQSummitRepositoryImpl
import com.nfq.data.remote.repository.TranslationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindEventRepository(
        eventRepositoryImpl: EventRepositoryImpl
    ): EventRepository

    @Binds
    @Singleton
    abstract fun bindBlogRepository(
        blogRepositoryImpl: BlogRepositoryImpl
    ): BlogRepository

    @Binds
    @Singleton
    abstract fun bindAttractionRepository(
        attractionRepositoryImpl: ExploreRepositoryImpl
    ): ExploreRepository

    @Binds
    @Singleton
    abstract fun bindTranslationRepository(
        translationRepositoryImpl: TranslationRepositoryImpl
    ): TranslationRepository

    @Binds
    @Singleton
    abstract fun bindNFQSummitRepository(
        nfqSummitRepositoryImpl: NFQSummitRepositoryImpl
    ): NFQSummitRepository
}