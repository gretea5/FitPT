package com.ssafy.domain.model.measure

data class CompositionItem(
    val compositionLogId: Int,
    val memberId: Int,
    val createdAt: String,
    val protein: Double,
    val bmr: Double,
    val mineral: Double,
    val bodyAge: Int,
    val smm: Double,
    val icw: Double,
    val ecw: Double,
    val bfm: Double,
    val bfp: Double,
    val weight: Double
)