package com.ssafy.domain.model.report

import com.ssafy.domain.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PtReportItem(
    val reportId: Int,
    val dateTime: String,
    val trainerName: String
): BaseModel