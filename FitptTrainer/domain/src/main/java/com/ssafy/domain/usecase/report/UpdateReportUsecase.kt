package com.ssafy.domain.usecase.report

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.report.Report
import com.ssafy.domain.repository.report.ReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateReportUsecase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(reportId: Int, report: Report): Flow<ResponseStatus<Int>> {
        return reportRepository.updateReport(reportId, report)
    }
}