package com.ssafy.data.di

import com.ssafy.data.repository.auth.AuthRepositoryImpl
import com.ssafy.data.repository.gym.GymRepositoryImpl
import com.ssafy.data.repository.home.ScheduleRepositoryImpl
import com.ssafy.data.repository.measure.BodyRepositoryImpl
import com.ssafy.data.repository.report.ReportRepositoryImpl
import com.ssafy.data.repository.user.DataStoreRepositoryImpl
import com.ssafy.data.repository.user.UserRepositoryImpl
import com.ssafy.domain.repository.auth.AuthRepository
import com.ssafy.domain.repository.gym.GymRepository
import com.ssafy.domain.repository.home.ScheduleRepository
import com.ssafy.domain.repository.measure.BodyRepository
import com.ssafy.domain.repository.report.ReportRepository
import com.ssafy.domain.repository.user.DataStoreRepository
import com.ssafy.domain.repository.user.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [NetworkModule::class, DataStoreModule::class])
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindGymRepository(
        gymRepositoryImpl: GymRepositoryImpl
    ): GymRepository


    @Binds
    @Singleton
    abstract fun bindBodyRepository(
        bodyRepositoryImpl: BodyRepositoryImpl
    ): BodyRepository


    @Binds
    @Singleton
    abstract fun bindReportRepository(
        reportRepositoryImpl: ReportRepositoryImpl
    ): ReportRepository

    @Binds
    @Singleton
    abstract fun bindScheduleRepository(
        scheduleRepositoryImpl: ScheduleRepositoryImpl
    ): ScheduleRepository

    @Binds
    @Singleton
    abstract fun bindDataStoreRepository(
        dataStoreRepositoryImpl: DataStoreRepositoryImpl
    ): DataStoreRepository
}