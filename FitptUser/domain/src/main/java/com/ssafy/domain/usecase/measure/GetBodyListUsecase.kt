package com.ssafy.domain.usecase.measure

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.domain.model.measure.CompositionItem
import com.ssafy.domain.repository.measure.BodyRepository
import com.ssafy.domain.repository.user.DataStoreRepository
import com.ssafy.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetBodyListUsecase @Inject constructor(private val bodyRepository: BodyRepository, private val dataStoreRepository: DataStoreRepository){
    suspend operator fun invoke(sort :String,order: String): Flow<ResponseStatus<List<CompositionItem>>> {
        val memberId = dataStoreRepository.userId.firstOrNull() ?: -1
        return bodyRepository.getBodyList(memberId.toInt(),sort,order)
    }
}