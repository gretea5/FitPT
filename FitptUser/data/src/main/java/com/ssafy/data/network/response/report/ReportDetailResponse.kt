package com.ssafy.data.network.response.report

import com.ssafy.data.network.common.BaseResponse
import com.ssafy.data.network.mapper.DataMapper
import com.ssafy.domain.model.base.BaseModel
import com.ssafy.domain.model.report.CompositionResponseDto
import com.ssafy.domain.model.report.ReportDetailInfo
import com.ssafy.domain.model.report.ReportExercise
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportDetailResponse(
    val compositionResponseDto: CompositionResponseDto,
    val createdAt: String,
    val memberId: Int,
    val reportComment: String,
    val reportExercises: List<ReportExercise>,
    val reportId: Int,
    val trainerName: String
): BaseResponse {
    companion object : DataMapper<ReportDetailResponse, ReportDetailInfo> {
        override fun ReportDetailResponse.toDomainModel(): ReportDetailInfo {
            return ReportDetailInfo(
                compositionResponseDto = this.compositionResponseDto,
                createdAt = this.createdAt,
                reportComment = this.reportComment,
                reportExercises = this.reportExercises,
                reportId = this.reportId
            )
        }
    }
}