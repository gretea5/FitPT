package com.ssafy.data.network.api

import com.ssafy.data.network.request.schedule.ScheduleRequest
import com.ssafy.data.network.response.schedule.ScheduleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ScheduleService {
    @GET("schedules")
    suspend fun getSchedules(
        @Query("date") date: String? = null,
        @Query("month") month: String? = null,
        @Query("trainerId") trainerId: Long? = null,
        @Query("memberId") memberId: Long? = null
    ) : Response<List<ScheduleResponse>>

    @POST("schedules")
    suspend fun createSchedule(@Body scheduleRequest: ScheduleRequest) : Response<Long>

    @DELETE("schedules/{scheduleId}")
    suspend fun deleteSchedule(@Path("scheduleId") scheduleId: Long) : Response<Long>
}