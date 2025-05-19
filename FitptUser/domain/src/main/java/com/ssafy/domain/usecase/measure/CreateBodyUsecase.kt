package com.ssafy.domain.usecase.measure

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.measure.CompositionDetail
import com.ssafy.domain.repository.measure.BodyRepository
import com.ssafy.domain.repository.user.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateBodyUsecase @Inject constructor(private val bodyRepository: BodyRepository, private val dataStoreRepository: DataStoreRepository){
    suspend operator fun invoke(compositionDetail: CompositionDetail): Flow<ResponseStatus<Int>> {
        return bodyRepository.createBody(compositionDetail)
    }
}