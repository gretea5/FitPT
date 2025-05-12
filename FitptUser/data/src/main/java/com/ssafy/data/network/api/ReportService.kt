package com.ssafy.data.network.api

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportService {
    @GET("reports")
    suspend fun getReportList(@Query("memberId") memberId: Int) :Response<Unit>

    @POST("reports")
    suspend fun createReport() : Response<Unit>

    @GET("reports/{reportId}")
    suspend fun getDetailReport(@Path("reportId") reportId: Int) : Response<Unit>

    @PATCH("reports/{reportsId")
    suspend fun updateReport() : Response<Unit>
}