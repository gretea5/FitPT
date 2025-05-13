package com.ssafy.domain.repository.report

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.report.PtReportItem
import com.ssafy.domain.model.report.ReportDetailInfo
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    suspend fun getReportList(): Flow<ResponseStatus<List<PtReportItem>>>
    suspend fun getReportDetailInfo(): Flow<ResponseStatus<ReportDetailInfo>>
}