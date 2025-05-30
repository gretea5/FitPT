package com.ssafy.data.network.common

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class ApiResponseHandler {
    suspend fun <T> handle(call: suspend () -> Response<T>): Flow<ApiResponse<T>> {
        return flow {
            val response = call.invoke()
            if (response.isSuccessful && response.body() != null) {
                emit(ApiResponse.Success(response.body()!!))
            } else {
                val errorBody = response.errorBody()?.string()
                val type = object : TypeToken<ErrorResponse>() {}.type
                val errorResponse: ErrorResponse? = Gson().fromJson(errorBody, type)

                if (errorResponse != null) {
                    emit(ApiResponse.Error(errorResponse))
                } else {
                    val message = response.message()
                    emit(
                        ApiResponse.Error(
                            ErrorResponse(
                                status = response.code().toString(),
                                error = "UNKNOWN_ERROR",
                                code = "UNKNOWN_CODE",
                                message = message ?: "알 수 없는 오류가 발생했습니다."
                            )
                        )
                    )
                }
            }
        }
    }
}