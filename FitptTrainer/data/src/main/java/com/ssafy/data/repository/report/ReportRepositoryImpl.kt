package com.ssafy.data.repository.report

import com.ssafy.data.network.api.ReportService
import com.ssafy.data.network.common.ApiResponse
import com.ssafy.data.network.common.ApiResponseHandler
import com.ssafy.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.report.Report
import com.ssafy.domain.model.report.ReportDetail
import com.ssafy.domain.model.report.ReportList
import com.ssafy.domain.repository.report.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val reportService: ReportService
) : ReportRepository {
    override suspend fun getReportList(memberId: Int): Flow<ResponseStatus<List<ReportList>>> = flow {
        val responseFlow = ApiResponseHandler().handle {
            reportService.getReportList(memberId)
        }

        responseFlow.collect { result ->
            when (result) {
                is ApiResponse.Success -> {
                    emit(ResponseStatus.Success(result.data))
                }

                is ApiResponse.Error -> {
                    emit(ResponseStatus.Error(result.error.toDomainModel()))
                }
            }
        }
    }

    override suspend fun createReport(report: Report): Flow<ResponseStatus<Int>> {
        return flow {
            val responseFlow = ApiResponseHandler().handle {
                reportService.createReport(report)
            }

            responseFlow.collect { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        emit(ResponseStatus.Success(result.data))
                    }

                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }
        }
    }

    override suspend fun getReportDetail(reportId: Int): Flow<ResponseStatus<ReportDetail>> = flow {
        val responseFlow = ApiResponseHandler().handle {
            reportService.getReportDetail(reportId)
        }

        responseFlow.collect { result ->
            when (result) {
                is ApiResponse.Success -> {
                    emit(ResponseStatus.Success(result.data))
                }

                is ApiResponse.Error -> {
                    emit(ResponseStatus.Error(result.error.toDomainModel()))
                }
            }
        }
    }

    override suspend fun updateReport(reportId: Int, report: Report): Flow<ResponseStatus<Int>> = flow {
        val responseFlow = ApiResponseHandler().handle {
            reportService.updateReport(reportId, report)
        }

        responseFlow.collect { result ->
            when (result) {
                is ApiResponse.Success -> {
                    emit(ResponseStatus.Success(result.data))
                }

                is ApiResponse.Error -> {
                    emit(ResponseStatus.Error(result.error.toDomainModel()))
                }
            }
        }
    }
}

