package com.ssafy.domain.usecase.measure

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.measure.CompositionDetail
import com.ssafy.domain.model.measure.CompositionItem
import com.ssafy.domain.repository.measure.BodyRepository
import com.ssafy.domain.repository.user.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetBodyDetailUsecase @Inject constructor(private val bodyRepository: BodyRepository){
    suspend operator fun invoke(compositionLogId :Int): Flow<ResponseStatus<CompositionItem>> {
        return bodyRepository.getBodyDetail(compositionLogId)
    }
}