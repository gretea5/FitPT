package com.ssafy.data.network.api

import com.ssafy.data.network.response.ScheduleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ScheduleService {
    @GET("schedules")
    suspend fun getSchedules(
        @Query("date") date: String? = null,
        @Query("month") month: String? = null,
        @Query("trainerId") trainerId: Long? = null,
        @Query("memberId") memberId: Long? = null
    ) : Response<List<ScheduleResponse>>
}