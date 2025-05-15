package com.ssafy.domain.repository.report

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.report.Report
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    suspend fun createReport(report: Report): Flow<ResponseStatus<Int>>
}