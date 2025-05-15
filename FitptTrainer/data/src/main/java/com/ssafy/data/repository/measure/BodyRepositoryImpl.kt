package com.ssafy.data.repository.measure

import com.ssafy.data.network.api.BodyService
import com.ssafy.data.network.common.ApiResponse
import com.ssafy.data.network.common.ApiResponseHandler
import com.ssafy.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.measure.CompositionDetail
import com.ssafy.domain.model.measure.CompositionItem
import com.ssafy.domain.repository.measure.BodyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class BodyRepositoryImpl  @Inject constructor(
    private val bodyService: BodyService,
): BodyRepository {

    override suspend fun getBodyList(
        memberId: Int,
        sort: String,
        order: String
    ): Flow<ResponseStatus<List<CompositionItem>>> {
        return flow {
            val result = ApiResponseHandler().handle {

                bodyService.getBodyList(memberId,sort,order)
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success ->
                    emit(ResponseStatus.Success(result.data))
                is ApiResponse.Error -> emit(ResponseStatus.Error(result.error.toDomainModel()))
            }
        }
    }

    override suspend fun createBody(compositionDetail: CompositionDetail): Flow<ResponseStatus<Unit>> {
        return flow {
            val result = ApiResponseHandler().handle {
                bodyService.createBody(compositionDetail)
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success -> emit(ResponseStatus.Success(result.data))
                is ApiResponse.Error -> emit(ResponseStatus.Error(result.error.toDomainModel()))
            }
        }
    }

    override suspend fun getBodyDetail(compositionLogId: Int): Flow<ResponseStatus<CompositionItem>> {
        return flow {
            val result = ApiResponseHandler().handle {
                bodyService.getBodyDetailInfo(compositionLogId)
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success -> emit(ResponseStatus.Success(result.data))
                is ApiResponse.Error -> emit(ResponseStatus.Error(result.error.toDomainModel()))
            }
        }
    }

}



