package com.ssafy.data.network.api

import com.ssafy.domain.model.measure.CompositionDetail
import com.ssafy.domain.model.measure.CompositionItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BodyService {
    @GET("body-composition")
    suspend fun getBodyList(@Query("memberId") memberId: Int,@Query("sort") sort :String,@Query("order") order: String) : Response<List<CompositionItem>>

    @POST("body-composition")
    suspend fun createBody(@Body compositionDetail: CompositionDetail) : Response<Unit>

    @GET("body-composition/{compositionLogId}")
    suspend fun getBodyDetailInfo(@Path("compositionLogId") compositionLogId: Int) : Response<CompositionItem>

}