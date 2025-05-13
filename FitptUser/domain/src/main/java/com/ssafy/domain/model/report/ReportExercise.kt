package com.ssafy.domain.model.report

import android.os.Parcelable
import com.ssafy.domain.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReportExercise(
    val activation_muscle_id: List<Long>,
    val exerciseAchievement: String,
    val exerciseComment: String,
    val exerciseName: String
)  : BaseModel