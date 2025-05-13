package com.ssafy.presentation.report.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "HealthReportViewModel_FitPT"

@HiltViewModel
class HealthReportViewModel @Inject constructor() : ViewModel() {

    private val _name = MutableLiveData<String>("")
    val name: LiveData<String> = _name

    private val _score = MutableLiveData<String>("")
    val score: LiveData<String> = _score

    private val _description = MutableLiveData<String>("")
    val description: LiveData<String> = _description

    private val _isAnyMuscleSelected = MutableLiveData<Boolean>(false)
    val isAnyMuscleSelected: LiveData<Boolean> = _isAnyMuscleSelected

    private val _isAddButtonEnabled = MediatorLiveData<Boolean>().apply {
        fun check() {
            value = isValidInput(
                _name.value,
                _score.value,
                _description.value,
                _isAnyMuscleSelected.value
            )
        }

        addSource(_name) { check() }
        addSource(_score) { check() }
        addSource(_description) { check() }
        addSource(_isAnyMuscleSelected) { check() }
    }

    val isAddButtonEnabled: LiveData<Boolean> = _isAddButtonEnabled

    private fun isValidInput(
        name: String?,
        score: String?,
        description: String?,
        muscleSelected: Boolean?
    ): Boolean {
        return !name.isNullOrBlank() &&
                !score.isNullOrBlank() &&
                !description.isNullOrBlank() &&
                muscleSelected == true
    }

    fun updateName(newName: String) {
        _name.value = newName
    }

    fun updateScore(newScore: String) {
        _score.value = newScore
    }

    fun updateDescription(desc: String) {
        _description.value = desc
    }

    fun updateMuscleSelection(isSelected: Boolean) {
        _isAnyMuscleSelected.value = isSelected
    }

    fun clearInputs() {
        _name.value = ""
        _score.value = ""
        _description.value = ""
        _isAnyMuscleSelected.value = false
    }
}
