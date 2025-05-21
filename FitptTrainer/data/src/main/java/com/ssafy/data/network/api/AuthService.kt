package com.ssafy.data.network.api

import com.ssafy.data.network.request.TrainerLoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("trainers")
    suspend fun login(@Body trainerLoginRequest: TrainerLoginRequest) : Response<Unit>
}