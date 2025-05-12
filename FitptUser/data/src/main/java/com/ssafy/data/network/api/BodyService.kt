package com.ssafy.data.network.api

import com.ssafy.data.network.request.CompositionRequest
import com.ssafy.data.network.response.measure.CompositionListReponse
import com.ssafy.data.network.response.measure.CompositionListReponseItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BodyService {
    @GET("body-composition")
    suspend fun getBodyList(@Query("memberId") memberId: Int,@Query("sort") sort :String,@Query("order") order: String) : Response<List<CompositionListReponseItem>>

    @POST("body-composition")
    suspend fun createBody(@Body compositionRequest: CompositionRequest) : Response<Unit>

    @GET("body-composition/{compositionLogId}")
    suspend fun getBodyDetailInfo(@Path("compositionLogId") compositionLogId: Int) : Response<CompositionListReponseItem>

}