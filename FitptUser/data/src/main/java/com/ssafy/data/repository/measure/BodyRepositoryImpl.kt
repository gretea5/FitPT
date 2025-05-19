package com.ssafy.data.repository.measure

import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.data.network.api.BodyService
import com.ssafy.data.network.api.UserService
import com.ssafy.data.network.common.ApiResponse
import com.ssafy.data.network.common.ApiResponseHandler
import com.ssafy.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.measure.CompositionDetail
import com.ssafy.domain.model.measure.CompositionItem
import com.ssafy.domain.repository.measure.BodyRepository
import com.ssafy.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import android.util.Log
import com.ssafy.domain.model.measure.CompositionDetailInfo

private const val TAG = "BodyRepositoryImpl"
internal class BodyRepositoryImpl  @Inject constructor(
    private val bodyService: BodyService,
    private val dataStore: UserDataStoreSource
): BodyRepository {
    override suspend fun getBodyList(
        sort: String,
        order: String
    ): Flow<ResponseStatus<List<CompositionItem>>> {
        return flow {
            val result = ApiResponseHandler().handle {
                val user = dataStore.user.firstOrNull()
                bodyService.getBodyList(user!!.memberId,sort,order)
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success ->
                    emit(ResponseStatus.Success(result.data))
                is ApiResponse.Error -> emit(ResponseStatus.Error(result.error.toDomainModel()))
            }
        }
    }

    override suspend fun createBody(compositionDetail: CompositionDetail): Flow<ResponseStatus<Int>> {
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

    override suspend fun getBodyDetail(compositionLogId: Int): Flow<ResponseStatus<CompositionDetailInfo>> {
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



