package com.ssafy.domain.model.report

import android.os.Parcelable
import com.ssafy.domain.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReportDetailInfo(
    val compositionResponseDto: CompositionResponseDto,
    val createdAt: String,
    val reportComment: String,
    val reportExercises: List<ReportExercise>,
    val reportId: Int
) : BaseModel
