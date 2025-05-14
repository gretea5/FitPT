package com.ssafy.presentation.report.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.domain.model.report.HealthReportWorkout
import com.ssafy.domain.model.report.TempHealthReportWorkout
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "ReportViewModel_FitPT"

@HiltViewModel
class ReportViewModel @Inject constructor() : ViewModel() {
    private val _reportExercises = MutableLiveData<List<HealthReportWorkout>>()
    val reportExercises: LiveData<List<HealthReportWorkout>> = _reportExercises

    private val _reportComment = MutableLiveData<String>()
    val reportComment: LiveData<String> = _reportComment

    private val _reportMeasureId = MutableLiveData<Int>()
    val reportMeasureId: LiveData<Int> = _reportMeasureId

    private val _isReportDataFilled = MediatorLiveData<Boolean>().apply {
        fun update() {
            val exercises = _reportExercises.value
            val comment = _reportComment.value
            value = !exercises.isNullOrEmpty() || !comment.isNullOrBlank()
        }

        addSource(_reportExercises) { update() }
        addSource(_reportComment) { update() }
    }
    val isReportDataFilled: LiveData<Boolean> = _isReportDataFilled

    fun setReportExercises(items: List<TempHealthReportWorkout>) {
        val reportItems = items.map { item ->
            HealthReportWorkout(
                exerciseName = item.exerciseName,
                exerciseAchievement = item.exerciseAchievement,
                exerciseComment = item.exerciseComment,
                activationMuscleId = item.activationMuscleId
            )
        }
        _reportExercises.value = reportItems

        Log.d(TAG, "setReportExercises: ${_reportExercises.value}")
    }

    fun setReportComment(comment: String){
        _reportComment.value = comment
    }
}