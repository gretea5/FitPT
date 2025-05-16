package com.ssafy.domain.model.home

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScheduleInfo(
    val endTime: String,
    val memberId: Int,
    val scheduleContent: String,
    val scheduleId: Int,
    val startTime: String,
    val trainerId: Int,
    val trainerName: String
) : Parcelable