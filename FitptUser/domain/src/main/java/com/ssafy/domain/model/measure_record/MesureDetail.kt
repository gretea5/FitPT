package com.ssafy.domain.model.measure_record

data class MesureDetail(
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
    val weight: Double,
    val weightDif: Double,
    val bfpDif: Double,
    val smmDif: Double
)