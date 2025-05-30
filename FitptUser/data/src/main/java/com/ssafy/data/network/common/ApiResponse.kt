package com.ssafy.data.network.common


sealed class ApiResponse<out T> {
    data class Success<T>(val data: T): ApiResponse<T>()
    data class Error(val error: ErrorResponse): ApiResponse<Nothing>()
}