package com.ssafy.data.di

import com.ssafy.data.network.api.AuthService
import com.ssafy.data.network.api.BodyService
import com.ssafy.data.network.api.GymService
import com.ssafy.data.network.api.ReportService
import com.ssafy.data.network.api.ScheduleService
import com.ssafy.data.network.api.UserService
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
        @NoInterceptorRetrofit retrofit: Retrofit
    ): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideBodyService(
        @InterceptorRetrofit retrofit: Retrofit
    ): BodyService {
        return retrofit.create(BodyService::class.java)
    }

    @Provides
    @Singleton
    fun provideGymService(
        @NoInterceptorRetrofit retrofit: Retrofit
    ): GymService {
        return retrofit.create(GymService::class.java)
    }

    @Provides
    @Singleton
    fun provideReportService(
        @InterceptorRetrofit retrofit: Retrofit
    ): ReportService {
        return retrofit.create(ReportService::class.java)
    }

    @Provides
    @Singleton
    fun provideScheduleService(
        @InterceptorRetrofit retrofit: Retrofit
    ): ScheduleService {
        return retrofit.create(ScheduleService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(
        @InterceptorRetrofit retrofit: Retrofit
    ): UserService {
        return retrofit.create(UserService::class.java)
    }
}
