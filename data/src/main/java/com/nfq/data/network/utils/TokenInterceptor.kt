package com.nfq.data.network.utils

import okhttp3.Interceptor
import okhttp3.Response

fun String.prefixBearer() = "Bearer $this"

fun tokenInterceptor(
    chain: Interceptor.Chain
): Response {
    val originalRequest = chain.request()
    val request = originalRequest.newBuilder()
        .addHeader("Content-Type", "application/json")
        .addHeader("Accept", "application/json")
        .addHeader("X-App-Type", "summit-app")
        .addHeader("Token", "6|PQpuioTJIobBHUNrCKfRxPYskZKJNW5eyR8jFNRm7c08a57e")
        .build()
    return chain.proceed(request = request)
}