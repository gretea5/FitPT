package com.ssafy.data.network.interceptor


import com.ssafy.data.datasource.TrainerDataStoreSource
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class RequestInterceptor constructor(private val dataStore: TrainerDataStoreSource): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            dataStore.jwtToken.firstOrNull() ?: ""
        }
        val requestWithToken = chain.request().newBuilder()
            .addHeader("Authorization", token)
            .build()

        return chain.proceed(requestWithToken)
    }
}