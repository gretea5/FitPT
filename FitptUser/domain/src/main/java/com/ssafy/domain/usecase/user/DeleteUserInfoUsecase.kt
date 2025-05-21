package com.ssafy.domain.usecase.user

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteUserInfoUsecase @Inject constructor(private val userRepository: UserRepository){
    suspend operator fun invoke(): Flow<ResponseStatus<Unit>> {
        return userRepository.deleteUserInfo()
    }
}