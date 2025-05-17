package com.ssafy.presentation.report.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.model.base.ResponseStatus
import com.ssafy.domain.model.measure.CompositionDetail
import com.ssafy.domain.model.measure.CompositionItem
import com.ssafy.domain.usecase.measure.CreateBodyUsecase
import com.ssafy.domain.usecase.measure.GetBodyDetailUsecase
import com.ssafy.domain.usecase.measure.GetBodyListUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MeasureViewModel"
@HiltViewModel
class MeasureViewModel @Inject constructor(
    private val createBodyUsecase: CreateBodyUsecase,
) : ViewModel() {

    private val _measureCreateInfo = MutableStateFlow<CreateBodyInfoState>(CreateBodyInfoState.Initial)
    val measureCreateInfo: StateFlow<CreateBodyInfoState> = _measureCreateInfo.asStateFlow()


    fun createBody(userDetail: CompositionDetail) {
        viewModelScope.launch {
            createBodyUsecase(userDetail)
                .onStart {  }
                .catch { e ->

                }
                .firstOrNull()
                .let { uiState ->
                    when(uiState) {
                        is ResponseStatus.Success -> {
                            Log.d(TAG,uiState.data.toString())
                            _measureCreateInfo.value = CreateBodyInfoState.Success(uiState.data)
                        }
                        is ResponseStatus.Error -> {
                            Log.d(TAG,uiState.error.message)
                        }
                        else -> Log.d("MeasureViewModel", "fetchUser: else error")
                    }
                }
        }
    }
}

sealed class CreateBodyInfoState {
    object Initial: CreateBodyInfoState()
    object Loading: CreateBodyInfoState()
    data class Success(val measureCreate: Int): CreateBodyInfoState()
    data class Error(val message: String): CreateBodyInfoState()
}