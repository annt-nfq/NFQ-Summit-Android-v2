package com.nfq.data.network.utils

import com.nfq.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

fun String.prefixBearer() = "Bearer $this"

fun tokenInterceptor(
    chain: Interceptor.Chain
): Response {
    val originalRequest = chain.request()
    val request = originalRequest.newBuilder()
        .addHeader("Authorization", BuildConfig.BASIC_TOKEN.prefixBearer())
        .addHeader("Content-Type", "application/json")
        .addHeader("Accept", "application/json")
        .addHeader("X-App-Type", "summit-app")
        .build()
    return chain.proceed(request = request)
}