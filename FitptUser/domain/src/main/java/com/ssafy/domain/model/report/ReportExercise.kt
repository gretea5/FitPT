package com.ssafy.domain.model.report

import android.os.Parcelable
import com.ssafy.domain.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReportExercise(
    val activationMuscleId: List<Long>,
    val exerciseAchievement: String,
    val exerciseComment: String,
    val exerciseName: String
)  : BaseModel