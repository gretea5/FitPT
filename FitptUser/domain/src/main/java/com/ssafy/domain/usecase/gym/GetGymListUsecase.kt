package com.ssafy.domain.usecase.gym

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.Gym
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.domain.repository.gym.GymRepository
import com.ssafy.domain.repository.user.DataStoreRepository
import com.ssafy.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject


class GetGymListUsecase @Inject constructor(private val gymRepository: GymRepository, private val dataStoreRepository: DataStoreRepository){
    suspend operator fun invoke(keyword: String): Flow<ResponseStatus<List<Gym>>> {
        return gymRepository.gymSearchList(keyword)
    }
}
