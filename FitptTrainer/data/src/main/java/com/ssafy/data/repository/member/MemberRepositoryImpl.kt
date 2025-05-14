package com.ssafy.data.repository.member

import android.util.Log
import com.ssafy.data.network.api.MemberService
import com.ssafy.data.network.common.ApiResponse
import com.ssafy.data.network.common.ApiResponseHandler
import com.ssafy.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.data.network.response.member.MemberResponse.Companion.toDomainModel
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.member.MemberInfo
import com.ssafy.domain.repository.member.MemberRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val TAG = "MemberRepositoryImpl_ssafy"
class MemberRepositoryImpl @Inject constructor(
    private val memberService: MemberService
) : MemberRepository{
    override suspend fun getMemberInfoById(memberId: Long): Flow<ResponseStatus<MemberInfo>> {
        Log.d(TAG, "getMemberInfoById: ")
        return flow {
            val responseFlow = ApiResponseHandler().handle {
                Log.d(TAG, "getMemberInfoById: $memberId")
                memberService.getMemberInfoById(memberId)
            }

            responseFlow.collect { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        Log.d(TAG, "getMemberInfoById Success: ${result.data}")
                        emit(ResponseStatus.Success(result.data.toDomainModel()))
                    }
                    is ApiResponse.Error -> {
                        Log.d(TAG, "getMemberInfoById Error: ${result.error}")
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }
        }
    }
}