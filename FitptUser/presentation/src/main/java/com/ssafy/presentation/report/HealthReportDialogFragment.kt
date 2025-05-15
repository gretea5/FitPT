package com.ssafy.presentation.report

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.domain.model.report.ReportDetailInfo
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.DialogHealthReportBinding
import com.ssafy.presentation.report.adapter.ExerciseReportAdapter
import com.ssafy.presentation.report.viewModel.ReportDetailState
import com.ssafy.presentation.report.viewModel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "HealthReportDialogFragm"
@AndroidEntryPoint
class HealthReportDialogFragment : DialogFragment() {
        private var _binding: DialogHealthReportBinding? = null
        private val binding get() = _binding!!
        private val reportViewModel: ReportViewModel by activityViewModels()
        private var muscleId: Long = -1L

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = DialogHealthReportBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            initView()
        }

        override fun onStart() {
            super.onStart()
            dialog?.window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            arguments?.let {
                muscleId = it.getLong("muscleId", -1L)
            }
        }

        fun initView(){
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    reportViewModel.getReportDetailInfo.collect { state ->
                        when (state) {
                            is ReportDetailState.Success -> {
                                Log.d(TAG,state.reportDetail.toString())
                                Log.d(TAG,muscleId.toString())
                                setupExerciseRecyclerView(state.reportDetail)
                                //Log.d(com.ssafy.presentation.report.viewpager.TAG, state.reportDetail.toString())
                                //val activationMuscleIds = state.reportDetail.reportExercises.flatMap { it.activation_muscle_id }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }

        private fun setupExerciseRecyclerView(reportDetail: ReportDetailInfo) {
            val filteredExercises = reportDetail.reportExercises.filter {
                it.activationMuscleId.contains(muscleId)
            }
            Log.d(TAG,"출력"+filteredExercises.toString())
            val adapter = ExerciseReportAdapter(filteredExercises)
            binding.rvExercise.layoutManager = LinearLayoutManager(requireContext())
            binding.rvExercise.adapter = adapter
        }
}