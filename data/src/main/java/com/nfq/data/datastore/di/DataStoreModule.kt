package com.nfq.data.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.nfq.data.datastore.model.AppConfigResponse
import com.nfq.data.datastore.serializer.AppConfigSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Module
    @InstallIn(SingletonComponent::class)
    object Provider {
        @Provides
        @Singleton
        fun providesAppConfigDataStore(
            @ApplicationContext context: Context,
            appConfigSerializer: AppConfigSerializer,
        ): DataStore<AppConfigResponse> =
            DataStoreFactory.create(
                serializer = appConfigSerializer,
                migrations = listOf(),
            ) {
                context.dataStoreFile("app_config.pb")
            }

    }
}