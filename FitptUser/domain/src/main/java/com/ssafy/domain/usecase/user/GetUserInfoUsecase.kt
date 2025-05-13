package com.ssafy.domain.usecase.user

import com.ssafy.domain.model.auth.JwtToken
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.domain.repository.auth.AuthRepository
import com.ssafy.domain.repository.user.DataStoreRepository
import com.ssafy.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetUserInfoUsecase @Inject constructor(private val userRepository: UserRepository){
    suspend operator fun invoke(): Flow<ResponseStatus<UserInfo>> {
        return userRepository.getUserInfo()
    }
}