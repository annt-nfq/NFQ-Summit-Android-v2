package com.nfq.nfqsummit.di

import com.nfq.data.domain.repository.ExploreRepository
import com.nfq.data.domain.repository.BlogRepository
import com.nfq.data.domain.repository.EventRepository
import com.nfq.data.domain.repository.TranslationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
abstract class FakeRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindEventRepository(
        fakeEventRepository: FakeEventRepository
    ): EventRepository

    @Binds
    @Singleton
    abstract fun bindBlogRepository(
        fakeBlogRepository: FakeBlogRepository
    ): BlogRepository

    @Binds
    @Singleton
    abstract fun bindFakeExploreRepository(
        fakeAttractionRepository: FakeExploreRepository
    ): ExploreRepository

    @Binds
    @Singleton
    abstract fun bindTranslationRepository(
        fakeTranslationRepository: FakeTranslationRepository
    ): TranslationRepository
}