package com.ssafy.data.network.api

import com.ssafy.data.network.request.TrainerLoginRequest
import com.ssafy.data.network.response.TrainerLoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("trainers/login")
    suspend fun login(@Body trainerLoginRequest: TrainerLoginRequest) : Response<TrainerLoginResponse>
}