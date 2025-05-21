package com.ssafy.data.repository.auth

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
    override suspend fun login(accessToken: String): Flow<ResponseStatus<Unit>> {
        return flow {
            ApiResponseHandler().handle {
                authService.login(
                    TrainerLoginRequest(
                        kakaoAccessToken = accessToken,
                    )
                )
            }.onEach { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        // dataStore.saveUserId(result.data.userId.toLong())
                        emit(ResponseStatus.Success(Unit))
                    }
                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }
}