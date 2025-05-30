package com.ssafy.domain.repository.measure

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.measure.CompositionDetail
import com.ssafy.domain.model.measure.CompositionDetailItem
import com.ssafy.domain.model.measure.CompositionItem
import kotlinx.coroutines.flow.Flow

interface BodyRepository {
    suspend fun createBody(compositionDetail: CompositionDetail) : Flow<ResponseStatus<Int>>
    suspend fun getComposition(memberId: Int, sort :String, order: String): Flow<ResponseStatus<List<CompositionItem>>>
    suspend fun getBodyDetail(compositionLogId: Int) : Flow<ResponseStatus<CompositionDetailItem>>
}