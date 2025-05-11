package com.ssafy.data.network.api

import com.ssafy.data.network.request.UserRequest
import com.ssafy.data.network.response.GetUserInfoResponse
import com.ssafy.domain.model.user.UserBodyInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {

    @POST("members")
    fun registerUser(@Body registerRequest: UserRequest):Response<Unit>

    @GET("members/{memberId}")
    fun getUserInfo(@Path("memberId") memberId : Int) : Response<GetUserInfoResponse>

    @PUT("members/{memberId}")
    fun updateUserInfo(@Path("memberId") memberId: Int,@Body userRequest: UserRequest) :Response<Unit>

    @DELETE("members/{memberId}")
    fun deleteUserInfo(@Path("memberId") memberId: Int) : Response<Unit>

    @PATCH("members/{memberId}")
    fun updatePartitionUserInfo(@Path("memberId") memberId: Int,@Body userBodyInfo: UserBodyInfo) :Response<Unit>
}