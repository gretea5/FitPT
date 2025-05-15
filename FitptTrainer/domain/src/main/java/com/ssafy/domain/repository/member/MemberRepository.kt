package com.ssafy.domain.repository.member

import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.member.MemberInfo
import kotlinx.coroutines.flow.Flow

interface MemberRepository {
    suspend fun getMemberInfoById(memberId: Long) : Flow<ResponseStatus<MemberInfo>>
    suspend fun getMembers() : Flow<ResponseStatus<List<MemberInfo>>>
}