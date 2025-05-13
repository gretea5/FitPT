package com.ssafy.presentation.report.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "HealthReportViewModel_FitPT"

@HiltViewModel
class HealthReportViewModel @Inject constructor(
    // usercase, datastore 추가 필요
) : ViewModel() {
    private val _name = MutableLiveData<String>("")
    val name: LiveData<String> = _name

    private val _score = MutableLiveData<String>("")
    val score: LiveData<String> = _score

    private val _isAddButtonEnabled = MediatorLiveData<Boolean>().apply {
        addSource(_name) { value = isValidInput(_name.value, _score.value) }
        addSource(_score) { value = isValidInput(_name.value, _score.value) }
    }
    val isAddButtonEnabled: LiveData<Boolean> = _isAddButtonEnabled

    private fun isValidInput(name: String?, score: String?): Boolean {
        return !name.isNullOrBlank() && !score.isNullOrBlank()
    }

    fun updateName(newName: String) {
        _name.value = newName
    }

    fun updateScore(newScore: String) {
        _score.value = newScore
    }

    fun clearInputs() {
        _name.value = ""
        _score.value = ""
    }
}