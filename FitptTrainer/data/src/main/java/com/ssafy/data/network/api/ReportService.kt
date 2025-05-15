package com.ssafy.data.network.api

import com.ssafy.domain.model.report.Report
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportService {
    @POST("reports")
    suspend fun createReport(@Body report: Report): Response<Int>
}
