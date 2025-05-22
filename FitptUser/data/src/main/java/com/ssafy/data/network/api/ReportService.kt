package com.ssafy.data.network.api

import com.ssafy.data.network.response.report.ReportDetailResponse
import com.ssafy.data.network.response.report.ReportListResponseItem
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportService {
    @GET("reports")
    suspend fun getReportList(@Query("memberId") memberId: Int) :Response<List<ReportListResponseItem>>

    @GET("reports/{reportId}")
    suspend fun getDetailReport(@Path("reportId") reportId: Int) : Response<ReportDetailResponse>
}