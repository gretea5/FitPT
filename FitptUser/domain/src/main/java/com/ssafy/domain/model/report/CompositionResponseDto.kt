package com.ssafy.domain.model.report

import android.os.Parcelable
import com.ssafy.domain.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CompositionResponseDto(
    val bfm: Double,
    val bfp: Double,
    val bmr: Double,
    val bodyAge: Int,
    val compositionLogId: Int,
    val createdAt: String,
    val ecw: Double,
    val icw: Double,
    val memberId: Int,
    val mineral: Double,
    val protein: Double,
    val smm: Double,
    val weight: Double
) :BaseModel