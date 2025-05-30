package com.ssafy.domain.repository.measure

import com.ssafy.domain.model.auth.JwtToken
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.measure.CompositionDetail
import com.ssafy.domain.model.measure.CompositionDetailInfo
import com.ssafy.domain.model.measure.CompositionItem
import kotlinx.coroutines.flow.Flow

interface BodyRepository {
    suspend fun getBodyList(sort :String,order: String): Flow<ResponseStatus<List<CompositionItem>>>
    suspend fun createBody(compositionDetail: CompositionDetail) : Flow<ResponseStatus<Int>>
    suspend fun getBodyDetail(compositionLogId: Int) : Flow<ResponseStatus<CompositionDetailInfo>>
}