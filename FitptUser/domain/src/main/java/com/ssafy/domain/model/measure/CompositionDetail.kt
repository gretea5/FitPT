package com.ssafy.domain.model.measure

data class CompositionDetail(
    val bfm: Double,
    val bfp: Double,
    val bmr: Double,
    val bodyAge: Int,
    val ecw: Double,
    val icw: Double,
    val memberId: Long,
    val mineral: Double,
    val protein: Double,
    val smm: Double,
    val weight: Double
)