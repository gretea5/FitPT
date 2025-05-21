package com.ssafy.domain.repository.gym

import com.ssafy.domain.model.auth.JwtToken
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.Gym
import kotlinx.coroutines.flow.Flow

interface GymRepository {
    suspend fun gymSearchList(keyword: String): Flow<ResponseStatus<List<Gym>>>
}