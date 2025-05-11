package com.ssafy.data.network.api

import com.ssafy.domain.model.login.Gym
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GymService {

    @GET("gyms")
    suspend fun searchGyms(
        @Query("keyword") keyword: String
    ): Response<List<Gym>>

}