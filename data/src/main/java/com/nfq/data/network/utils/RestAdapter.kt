package com.nfq.data.network.utils

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.nfq.data.BuildConfig
import com.nfq.data.database.dao.UserDao
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

inline fun <reified T> Retrofit.createService(): T = create(T::class.java)

fun createRetrofitClient(
    url: String,
    okHttpClient: OkHttpClient,
    networkJson: Json
): Retrofit = Retrofit.Builder()
    .baseUrl(url)
    .client(okHttpClient)
    .addConverterFactory(NullOnEmptyConverterFactory())
    .addConverterFactory(
        networkJson.asConverterFactory("application/json".toMediaType())
    ).build()


fun createOkHttpClient(
    context: Context,
    userDao: UserDao,
    httpLoggingInterceptor: HttpLoggingInterceptor,
) = OkHttpClient.Builder()
    .also {
        if (BuildConfig.DEBUG) {
            it.addInterceptor(
                httpLoggingInterceptor.apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
        }
    }
    .addInterceptor { chain ->
        tokenInterceptor(chain = chain, userDao = userDao)
    }
    .connectTimeout(60L, TimeUnit.SECONDS)
    .readTimeout(60L, TimeUnit.SECONDS)
    .writeTimeout(60L, TimeUnit.SECONDS)
    .build()
