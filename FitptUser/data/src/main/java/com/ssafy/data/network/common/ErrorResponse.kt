package com.ssafy.data.network.common

import com.ssafy.data.network.mapper.DataMapper
import com.ssafy.domain.model.base.NetworkError
import kotlinx.parcelize.Parcelize

@Parcelize
class ErrorResponse(
    val timestamp   : String? = null,
    val status      : String? = null,
    val error       : String? = null,
    val code        : String? = null,
    val message     : String? = null,
) : BaseResponse {
    companion object: DataMapper<ErrorResponse, NetworkError> {
        override fun ErrorResponse.toDomainModel(): NetworkError {
            return NetworkError(
                error = error ?: "NO_ERROR",
                code = code ?: "NO_CODE",
                status  = status ?: "NO_STATUS",
                message = message ?: "알 수 없는 에러"
            )
        }
    }
}