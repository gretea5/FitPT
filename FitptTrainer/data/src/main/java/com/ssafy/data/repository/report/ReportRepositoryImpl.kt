package com.ssafy.data.repository.report

import com.ssafy.data.network.api.ReportService
import com.ssafy.data.network.common.ApiResponse
import com.ssafy.data.network.common.ApiResponseHandler
import com.ssafy.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.report.Report
import com.ssafy.domain.repository.report.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val reportService: ReportService
) : ReportRepository {

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
}

