package com.ssafy.data.network.api

import com.ssafy.data.network.request.RegisterUserInfoRequest
import com.ssafy.data.network.request.UserLoginRequest
import com.ssafy.data.network.response.JwtTokenResponse
import com.ssafy.data.network.response.RegisterUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/kakao/login")
    suspend fun login(@Body userLoginRequest: UserLoginRequest) : Response<JwtTokenResponse>
    @POST("auth/kakao/signup")
    suspend fun signUp(@Body request: RegisterUserInfoRequest) : Response<RegisterUserResponse>
}