package com.ssafy.data.network.api

import com.ssafy.domain.model.home.ScheduleInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ScheduleService {
    @GET("schedules")
    suspend fun getSchedules(@Query("data") data: String, @Query("month") month: String) : Response<List<ScheduleInfo>>
}