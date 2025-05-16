package com.ssafy.data.di

import com.ssafy.data.repository.auth.AuthRepositoryImpl
import com.ssafy.data.repository.measure.BodyRepositoryImpl
import com.ssafy.data.repository.member.MemberRepositoryImpl
import com.ssafy.data.repository.report.ReportRepositoryImpl
import com.ssafy.data.repository.schedule.ScheduleRepositoryImpl
import com.ssafy.domain.repository.auth.AuthRepository
import com.ssafy.domain.repository.measure.BodyRepository
import com.ssafy.domain.repository.member.MemberRepository
import com.ssafy.domain.repository.report.ReportRepository
import com.ssafy.domain.repository.schedule.ScheduleRepository
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
    abstract fun bindScheduleRepository(
        scheduleRepositoryImpl: ScheduleRepositoryImpl
    ): ScheduleRepository

    @Binds
    @Singleton
    abstract fun bindMemberRepository(
        memberRepositoryImpl: MemberRepositoryImpl
    ): MemberRepository

    @Binds
    @Singleton
    abstract fun bindReportRepository(
        reportRepositoryImpl: ReportRepositoryImpl
    ): ReportRepository

    @Binds
    @Singleton
    abstract fun bindBodyRepository(
        bodyRepositoryImpl: BodyRepositoryImpl
    ): BodyRepository
}