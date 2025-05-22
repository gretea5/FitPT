package com.ssafy.domain.usecase.measure

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.measure.CompositionDetail
import com.ssafy.domain.repository.measure.BodyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateBodyUsecase @Inject constructor(private val bodyRepository: BodyRepository) {
    suspend operator fun invoke(compositionDetail: CompositionDetail): Flow<ResponseStatus<Int>> {
        return bodyRepository.createBody(compositionDetail)
    }
}