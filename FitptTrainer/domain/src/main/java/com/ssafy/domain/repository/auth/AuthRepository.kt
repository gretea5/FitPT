package com.ssafy.domain.repository.auth

import com.ssafy.domain.model.base.ResponseStatus
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(trainerLoginId : String, trainerPw: String): Flow<ResponseStatus<Long>>
}