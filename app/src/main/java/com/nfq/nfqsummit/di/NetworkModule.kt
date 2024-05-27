package com.nfq.nfqsummit.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import com.nfq.data.cache.SummitDatabase
import com.nfq.data.local.createDatabase
import com.nfq.data.local.createDriver
import com.nfq.nfqsummit.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            BuildConfig.SUPABASE_URL,
            BuildConfig.SUPABASE_KEY
        ) {
            defaultSerializer = KotlinXSerializer(Json {
                ignoreUnknownKeys = true
            })
            install(Auth)
            install(Postgrest)
        }
    }

    @Provides
    @Singleton
    fun provideDatabaseDriver(
        @ApplicationContext
        context: Context
    ): SqlDriver = createDriver(context)

    @Provides
    @Singleton
    fun provideDatabase(
        databaseDriver: SqlDriver
    ): SummitDatabase = createDatabase(databaseDriver)
}