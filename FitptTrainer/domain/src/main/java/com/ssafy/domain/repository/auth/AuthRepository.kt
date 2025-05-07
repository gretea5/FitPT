package com.ssafy.domain.repository.auth

import com.ssafy.domain.model.base.ResponseStatus
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(accessToken: String): Flow<ResponseStatus<Unit>>
}