package com.ssafy.data.network.api

import com.ssafy.data.network.response.member.MemberResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MemberService {
    @GET("members/{memberId}")
    suspend fun getMemberInfoById(@Path("memberId") memberId: Long): Response<MemberResponse>
}