package com.ssafy.presentation.report.viewmodel

import androidx.lifecycle.LiveData
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
    }
}