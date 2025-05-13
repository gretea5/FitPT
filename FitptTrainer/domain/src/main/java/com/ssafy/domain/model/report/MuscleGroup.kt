package com.ssafy.domain.model.report

import android.widget.ImageView

data class MuscleGroup(
    val key: String,
    val imageViews: List<ImageView>,
    var isSelected: Boolean = false
)
