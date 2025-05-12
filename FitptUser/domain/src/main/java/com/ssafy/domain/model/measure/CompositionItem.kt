package com.ssafy.domain.model.measure

data class CompositionItem(
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
)