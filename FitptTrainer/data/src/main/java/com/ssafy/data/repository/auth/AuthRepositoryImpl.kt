package com.ssafy.data.repository.auth

import android.util.Log
import com.ssafy.data.datasource.TrainerDataStoreSource
import com.ssafy.data.network.api.AuthService
import com.ssafy.data.network.common.ApiResponseHandler
import com.ssafy.data.network.common.ApiResponse
import com.ssafy.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.data.network.request.TrainerLoginRequest
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.repository.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val dataStore: TrainerDataStoreSource
): AuthRepository {
    override suspend fun login(trainerLoginId : String, trainerPw: String): Flow<ResponseStatus<Long>> {
        return flow {
            ApiResponseHandler().handle {
                authService.login(
                    TrainerLoginRequest(
                        trainerLoginId = trainerLoginId,
                        trainerPw = trainerPw,
                    )
                )
            }.onEach { result ->
                Log.d("AuthRepositoryImpl", "login: $result")
                when (result) {
                    is ApiResponse.Success -> {
                        //요청이 성공일때, 토큰이나 사용자 식별자 정보를 저장해야함
                        val trainerId = result.data as Long
                        // dataStore.saveUserId(result.data.userId.toLong())
                        emit(ResponseStatus.Success(trainerId))
                    }
                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }
}