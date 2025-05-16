package com.ssafy.domain.usecase.report

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.report.ReportDetail
import com.ssafy.domain.repository.report.ReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReportDetailUsecase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend fun invoke(reportId: Int): Flow<ResponseStatus<ReportDetail>> {
        return reportRepository.getReportDetail(reportId)
    }
}