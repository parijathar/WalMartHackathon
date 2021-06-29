package com.example.fiapp.network

import okhttp3.Interceptor
import okhttp3.Response

class JSONHeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .method(chain.request().method, chain.request().body)
            .build()

        return chain.proceed(request)
    }

}