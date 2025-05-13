package com.ssafy.data.repository.report

import android.util.Log
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.data.network.api.BodyService
import com.ssafy.data.network.api.ReportService
import com.ssafy.data.network.common.ApiResponse
import com.ssafy.data.network.common.ApiResponseHandler
import com.ssafy.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.data.network.response.GetUserInfoResponse.Companion.toDomainModel
import com.ssafy.data.network.response.report.toDomainModel
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.report.PtReportItem
import com.ssafy.domain.model.report.ReportDetailInfo
import com.ssafy.domain.repository.measure.BodyRepository
import com.ssafy.domain.repository.report.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class ReportRepositoryImpl  @Inject constructor(
    private val reportService: ReportService,
    private val dataStore: UserDataStoreSource
): ReportRepository {
    override suspend fun getReportList(): Flow<ResponseStatus<List<PtReportItem>>> {
        return flow {
            val result = ApiResponseHandler().handle {
                val memberId = dataStore.user.firstOrNull()!!.memberId
                reportService.getReportList(memberId)
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success -> {
                    val domainList = result.data.toDomainModel()// 리스트 변환
                    emit(ResponseStatus.Success(domainList))
                }
                is ApiResponse.Error -> emit(ResponseStatus.Error(result.error.toDomainModel()))
            }
        }
    }

    override suspend fun getReportDetailInfo(): Flow<ResponseStatus<ReportDetailInfo>> {
        TODO("Not yet implemented")
    }
}