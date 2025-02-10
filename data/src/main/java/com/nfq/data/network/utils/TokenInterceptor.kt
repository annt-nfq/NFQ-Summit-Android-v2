package com.nfq.data.network.utils

import com.nfq.data.BuildConfig
import com.nfq.data.database.dao.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

fun String.prefixBearer() = "Bearer $this"

fun tokenInterceptor(
    chain: Interceptor.Chain,
    userDao: UserDao
): Response {
    val originalRequest = chain.request()
    val tk = runBlocking(Dispatchers.IO) {
        userDao.getUser().first()?.tk ?: BuildConfig.BASIC_TOKEN
    }.prefixBearer()

    val request = originalRequest.newBuilder()
        .addHeader("Authorization", tk)
        .addHeader("Content-Type", "application/json")
        .addHeader("Accept", "application/json")
        .addHeader("X-App-Type", "summit-app")
        .build()
    return chain.proceed(request = request)
}