package com.nfq.data.database.di

import android.content.Context
import com.nfq.data.database.AppDatabase
import com.nfq.data.database.dao.AttractionDao
import com.nfq.data.database.dao.BlogDao
import com.nfq.data.database.dao.EventDao
import com.nfq.data.database.dao.UserDao
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


        @Provides
        @Singleton
        fun provideUserDao(appDatabase: AppDatabase): UserDao {
            return appDatabase.userDao()
        }

        @Provides
        @Singleton
        fun provideAttractionDao(appDatabase: AppDatabase): AttractionDao {
            return appDatabase.attractionDao()
        }

        @Provides
        @Singleton
        fun provideBlogDao(appDatabase: AppDatabase): BlogDao {
            return appDatabase.blogDao()
        }
    }
}