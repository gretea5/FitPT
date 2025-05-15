package com.ssafy.presentation.report.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.domain.model.member.MemberInfo
import com.ssafy.domain.model.report.HealthReportWorkout
import com.ssafy.domain.model.report.TempHealthReportWorkout
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "ReportViewModel_FitPT"

@HiltViewModel
class ReportViewModel @Inject constructor() : ViewModel() {

    // 이후 기본 값 삭제 필요
    private val _selectedMember = MutableLiveData<MemberInfo>(
        MemberInfo(
            memberId = 4,
            memberName = "string",
            memberGender = "남성",
            memberBirth = "1990-01-01",
            memberHeight = 170,
            memberWeight = 60,
            trainerId = 2,
            trainerName = "김동현",
            adminId = 2,
            gymName = "김동현집"
        )
    )
    val selectedMember: LiveData<MemberInfo> = _selectedMember

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

    fun setSeletedMember(memberInfo: MemberInfo) {
        _selectedMember.value = memberInfo
    }

    fun setReportComment(comment: String) {
        _reportComment.value = comment
    }
}