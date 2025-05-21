package com.ssafy.data.network.api

import com.ssafy.domain.model.report.Report
import com.ssafy.domain.model.report.ReportDetail
import com.ssafy.domain.model.report.ReportList
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReportService {
    @GET("reports")
    suspend fun getReportList(@Query("memberId") memberId: Int): Response<List<ReportList>>

    @POST("reports")
    suspend fun createReport(@Body report: Report): Response<Int>

    @GET("reports/{reportId}")
    suspend fun getReportDetail(@Path("reportId") reportId: Int): Response<ReportDetail>

    @PATCH("reports/{reportId}")
    suspend fun updateReport(@Path("reportId") reportId: Int, @Body report: Report): Response<Int>
}
