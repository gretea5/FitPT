package com.ssafy.domain.usecase.report

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.report.Report
import com.ssafy.domain.model.report.ReportList
import com.ssafy.domain.repository.report.ReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReportListUsecase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(memberId: Int): Flow<ResponseStatus<List<ReportList>>> {
        return reportRepository.getReportList(memberId)
    }
}