package com.ssafy.data.network.api

import retrofit2.http.GET

interface ScheduleService {
    @GET("schedules")
    suspend fun getSchedules()
}