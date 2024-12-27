package com.nfq.data.network.di

import android.content.Context
import com.nfq.data.BuildConfig
import com.nfq.data.database.dao.UserDao
import com.nfq.data.network.utils.createOkHttpClient
import com.nfq.data.network.utils.createRetrofitClient
import com.nfq.data.network.utils.networkConnectivity.ConnectivityObserver
import com.nfq.data.network.utils.networkConnectivity.NetworkConnectivityObserver
import com.nfq.data.remote.datasource.NFQSummitDataSource
import com.nfq.data.remote.datasource.NFQSummitDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Module
    @InstallIn(SingletonComponent::class)
    object Provider {
        @Provides
        @Singleton
        fun providesNetworkJson(): Json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }

        @Singleton
        @Provides
        fun providesCoroutineScope(): CoroutineScope {
            return CoroutineScope(SupervisorJob() + Dispatchers.Default)
        }

        @Provides
        @Singleton
        fun provideOkHttpClient(
            @ApplicationContext
            context: Context,
            userDao: UserDao
        ): OkHttpClient {
            return createOkHttpClient(
                context = context,
                userDao = userDao,
                httpLoggingInterceptor = HttpLoggingInterceptor()
            )
        }

        @Provides
        @Singleton
        fun provideRetrofit(okHttpClient: OkHttpClient, networkJson: Json): Retrofit {
            return createRetrofitClient(BuildConfig.BASE_URL, okHttpClient, networkJson)
        }

        @Provides
        @Singleton
        fun provideNetworkConnectivityObserver(
            @ApplicationContext context: Context
        ): ConnectivityObserver {
            return NetworkConnectivityObserver(context)
        }
    }

    @Binds
    abstract fun bindNFQSummitDataSource(
        nfqSummitDataSourceImpl: NFQSummitDataSourceImpl
    ): NFQSummitDataSource
}
