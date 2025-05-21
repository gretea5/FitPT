package com.ssafy.data.network.response.report

import com.ssafy.data.network.common.BaseResponse
import com.ssafy.data.network.mapper.DataMapper
import com.ssafy.data.network.response.report.ReportListResponseItem.Companion.toDomainModel
import com.ssafy.domain.model.report.PtReportItem
import com.ssafy.domain.model.report.ReportDetailInfo
import com.ssafy.domain.model.report.ReportExercise
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportListResponseItem(
    val compositionLogId: Int,
    val createdAt: String,
    val memberId: Int,
    val reportComment: String,
    val reportExercises: List<ReportExercise>,
    val reportId: Int,
    val trainerName: String
): BaseResponse{
    companion object : DataMapper<ReportListResponseItem, PtReportItem> {
        override fun ReportListResponseItem.toDomainModel(): PtReportItem {
            return PtReportItem(
                reportId = this.reportId,
                dateTime = this.createdAt,
                trainerName = this.trainerName
            )
        }
    }
}

fun List<ReportListResponseItem>.toDomainModel(): List<PtReportItem> {
    return this.map { it.toDomainModel() }
}