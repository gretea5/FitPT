package com.ssafy.presentation.report.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.domain.model.report.HealthReportWorkout
import com.ssafy.domain.model.report.MuscleGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _selectedMuscleIds = MutableStateFlow<List<Int>>(emptyList())
    val selectedMuscleIds: StateFlow<List<Int>> = _selectedMuscleIds

    private val _currentWorkoutId = MutableStateFlow<Long?>(null)
    val currentWorkoutId: StateFlow<Long?> = _currentWorkoutId

    private val _selectedWorkoutId = MutableStateFlow<Long?>(null)
    val selectedWorkoutId: StateFlow<Long?> = _selectedWorkoutId

    private val _selectedWorkoutItem = MutableLiveData<HealthReportWorkout?>()
    val selectedWorkoutItem: LiveData<HealthReportWorkout?> = _selectedWorkoutItem

    private val _workoutReportList = MutableStateFlow<List<HealthReportWorkout>>(emptyList())
    val workoutReportList: StateFlow<List<HealthReportWorkout>> = _workoutReportList

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

    fun addWorkoutReport() {
        val name = _name.value?.trim().orEmpty()
        val score = _score.value?.trim().orEmpty()
        val description = _description.value?.trim().orEmpty()
        val muscles = _selectedMuscleIds.value
        val id = _currentWorkoutId.value

        Log.d(TAG, "AddWorkoutReport: ${id}")

        if (id != null && name.isNotBlank() && score.isNotBlank() && description.isNotBlank() && muscles.isNotEmpty()) {
            val newReport = HealthReportWorkout(
                id = id,
                exerciseName = name,
                exerciseAchievement = score,
                exerciseComment = description,
                activationMuscleId = muscles.sorted(),
            )

            _workoutReportList.value = _workoutReportList.value + newReport

            Log.d(TAG, "addWorkoutReport: $newReport")
            clearInputs()

            _currentWorkoutId.value = null // 저장 후 초기화
        }
    }

    fun deleteSelectedWorkout() {
        _selectedWorkoutId.value?.let { id ->
            deleteWorkoutById(id)
        }
    }

    fun deleteWorkoutById(id: Long) {
        val updatedList = _workoutReportList.value.filterNot { it.id == id }
        _workoutReportList.value = updatedList

        // 삭제 후 선택 상태 초기화
        if (_selectedWorkoutId.value == id) {
            _selectedWorkoutId.value = null
            _selectedWorkoutItem.value = null
        }

        Log.d(TAG, "deleteWorkoutById: ${_workoutReportList.value}")
    }

    fun toggleMuscleSelection(muscleGroup: MuscleGroup) {
        muscleGroup.isSelected = !muscleGroup.isSelected

        val currentList = _selectedMuscleIds.value.toMutableList()
        if (muscleGroup.isSelected) {
            if (muscleGroup.id !in currentList) {
                currentList.add(muscleGroup.id)
            }
        } else {
            currentList.remove(muscleGroup.id)
        }

        _selectedMuscleIds.value = currentList
        _isAnyMuscleSelected.value = currentList.isNotEmpty()
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

    fun getWorkoutById(id: Long): HealthReportWorkout? {
        return _workoutReportList.value.find { it.id == id }
    }

    fun setCurrentWorkoutId(id: Long) {
        _currentWorkoutId.value = id
    }

    fun setSelectedWorkoutId(id: Long) {
        _selectedWorkoutId.value = id
        Log.d(TAG, "SelectedWorkoutId: ${_selectedWorkoutId.value}")

        val item = _workoutReportList.value.find { it.id == id }
        _selectedWorkoutItem.value = item
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
