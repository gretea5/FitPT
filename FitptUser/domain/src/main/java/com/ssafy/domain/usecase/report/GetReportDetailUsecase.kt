package com.ssafy.domain.usecase.report

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.report.PtReportItem
import com.ssafy.domain.model.report.ReportDetailInfo
import com.ssafy.domain.repository.report.ReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReportDetailUsecase @Inject constructor(private val reportRepository: ReportRepository){
    suspend operator fun invoke(reportId: Int): Flow<ResponseStatus<ReportDetailInfo>> {
        return reportRepository.getReportDetailInfo(reportId)
    }
}