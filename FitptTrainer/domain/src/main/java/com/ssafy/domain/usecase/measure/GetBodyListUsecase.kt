package com.ssafy.domain.usecase.measure

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.measure.CompositionItem
import com.ssafy.domain.repository.measure.BodyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBodyListUsecase @Inject constructor(private val bodyRepository: BodyRepository){
    suspend operator fun invoke(memberId: Int, sort :String,order: String): Flow<ResponseStatus<List<CompositionItem>>> {
        return bodyRepository.getComposition(memberId, sort,order)
    }
}