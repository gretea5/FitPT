package com.ssafy.data.repository.gym

import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.data.network.api.GymService
import com.ssafy.data.network.api.UserService
import com.ssafy.data.network.common.ApiResponse
import com.ssafy.data.network.common.ApiResponseHandler
import com.ssafy.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.data.network.response.GetUserInfoResponse.Companion.toDomainModel
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.Gym
import com.ssafy.domain.repository.gym.GymRepository
import com.ssafy.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class GymRepositoryImpl @Inject constructor(
    private val gymService: GymService,
    private val dataStore: UserDataStoreSource
): GymRepository {
    override suspend fun gymSearchList(keyword: String): Flow<ResponseStatus<List<Gym>>> {
        return flow {
            val result = ApiResponseHandler().handle {
                gymService.searchGyms(keyword)
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success -> emit(ResponseStatus.Success(result.data))
                is ApiResponse.Error -> emit(ResponseStatus.Error(result.error.toDomainModel()))
            }
        }
    }
}
