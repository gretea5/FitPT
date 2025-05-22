package com.ssafy.domain.usecase.report

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.measure.CompositionItem
import com.ssafy.domain.model.report.PtReportItem
import com.ssafy.domain.repository.measure.BodyRepository
import com.ssafy.domain.repository.report.ReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReportListUsecase @Inject constructor(private val reportRepository: ReportRepository){
    suspend operator fun invoke(): Flow<ResponseStatus<List<PtReportItem>>> {
        return reportRepository.getReportList()
    }
}