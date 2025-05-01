package com.ssafy.data.network.api

import com.ssafy.data.network.request.UserLoginRequest
import com.ssafy.data.network.response.JwtTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("users/login")
    suspend fun login(@Body userLoginRequest: UserLoginRequest) : Response<JwtTokenResponse>
}