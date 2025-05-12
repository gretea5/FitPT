package com.ssafy.data.network.response.measure

data class CompositionListReponseItem(
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