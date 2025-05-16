package com.ssafy.domain.repository.report

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.report.Report
import com.ssafy.domain.model.report.ReportDetail
import com.ssafy.domain.model.report.ReportList
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    suspend fun getReportList(memberId: Int): Flow<ResponseStatus<List<ReportList>>>
    suspend fun createReport(report: Report): Flow<ResponseStatus<Int>>
    suspend fun getReportDetail(reportId: Int): Flow<ResponseStatus<ReportDetail>>
    suspend fun updateReport(reportId: Int, report: Report): Flow<ResponseStatus<Int>>
}