package com.ssafy.data.repository.auth

import android.util.Log
import com.ssafy.data.datasource.TrainerDataStoreSource
import com.ssafy.data.network.api.AuthService
import com.ssafy.data.network.common.ApiResponseHandler
import com.ssafy.data.network.common.ApiResponse
import com.ssafy.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.data.network.request.trainer.TrainerLoginRequest
import com.ssafy.data.network.response.trainer.TrainerLoginResponse.Companion.toDomainModel
import com.ssafy.domain.model.auth.TrainerLogin
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
    override suspend fun login(trainerLoginId : String, trainerPw: String): Flow<ResponseStatus<TrainerLogin>> {
        return flow {
            ApiResponseHandler().handle {
                authService.login(
                    TrainerLoginRequest(
                        trainerLoginId = trainerLoginId,
                        trainerPw = trainerPw,
                    )
                )
            }.onEach { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        Log.d("token", result.data.accessToken)
                        dataStore.saveTrainerId(result.data.trainerId.toLong())
                        dataStore.saveJwtToken(result.data.accessToken)
                        emit(ResponseStatus.Success(result.data.toDomainModel()))
                    }
                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }
}