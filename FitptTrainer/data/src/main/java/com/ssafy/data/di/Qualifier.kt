package com.ssafy.data.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InterceptorRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NoInterceptorRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InterceptorOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NoInterceptorOkHttpClient