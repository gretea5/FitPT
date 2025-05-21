package com.ssafy.data.network.interceptor


import com.ssafy.data.datasource.UserDataStoreSource
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import android.util.Log
import javax.inject.Inject

class RequestInterceptor @Inject constructor(
    private val dataStore: UserDataStoreSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            dataStore.jwtToken.firstOrNull()
        }

        val requestBuilder = chain.request().newBuilder()

        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", token)
            Log.d("RequestInterceptor", "토큰 추가됨: $token")
        } else {
            Log.d("RequestInterceptor", "토큰 없음. 빈 요청")
        }

        return chain.proceed(requestBuilder.build())
    }
}