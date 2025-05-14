package com.ssafy.data.di

import com.ssafy.data.network.api.AuthService
import com.ssafy.data.network.api.ScheduleService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class, DataStoreModule::class])
@InstallIn(SingletonComponent::class)
internal class ApiModule {
    @Provides
    @Singleton
    fun provideAuthService(
        @InterceptorRetrofit retrofit: Retrofit
    ): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideScheduleService(
        @InterceptorRetrofit retrofit: Retrofit
    ): ScheduleService {
        return retrofit.create(ScheduleService::class.java)
    }
}