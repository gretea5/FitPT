package com.ssafy.domain.model.report

data class WorkoutNameScoreItem(
    val id: Long = System.currentTimeMillis(),
    var name: String = "",
    var score: String = "",
    var isEditing: Boolean = false,
    var isSelected: Boolean = false
)
