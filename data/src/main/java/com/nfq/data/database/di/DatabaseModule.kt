package com.nfq.data.database.di

import android.content.Context
import com.nfq.data.database.AppDatabase
import com.nfq.data.database.EventDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Module
    @InstallIn(SingletonComponent::class)
    object Provider {

        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context
        ): AppDatabase {
            return AppDatabase.buildDatabase(context)
        }

        @Provides
        @Singleton
        fun provideEventDao(appDatabase: AppDatabase): EventDao {
            return appDatabase.eventDao()
        }
    }
}