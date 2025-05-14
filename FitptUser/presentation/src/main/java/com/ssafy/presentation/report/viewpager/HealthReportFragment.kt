package com.ssafy.presentation.report.viewpager

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentHealthReportBinding
import com.ssafy.presentation.report.viewModel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ssafy.presentation.report.HealthReportDialogFragment
import com.ssafy.presentation.report.viewModel.ReportDetailState
import com.ssafy.presentation.util.CommonUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

private const val TAG = "HealthReportFragment"
@AndroidEntryPoint
class HealthReportFragment : BaseFragment<FragmentHealthReportBinding>(
    FragmentHealthReportBinding::bind,
    R.layout.fragment_health_report
) {
    private val reportViewModel : ReportViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initView()
    }

    fun initEvent(){
        binding.btnReportMusclesBack.setOnClickListener {
            binding.clReportHealthMusclesBack.isVisible = true
            binding.clReportHealthMusclesFront.isVisible = false
        }
        binding.btnReportMusclesFront.setOnClickListener {
            binding.clReportHealthMusclesBack.isVisible = false
            binding.clReportHealthMusclesFront.isVisible = true
        }
    }

    fun initView(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                reportViewModel.getReportDetailInfo.collect { state ->
                    when (state) {
                        is ReportDetailState.Success -> {
                            Log.d(TAG, state.reportDetail.toString())
                            val activationMuscleIds = state.reportDetail.reportExercises.flatMap { it.activation_muscle_id }
                            Log.d(TAG,activationMuscleIds.toString())
                            highlightMuscles(activationMuscleIds,requireView())
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun highlightMuscles(muscleData: List<Long>, fragmentView: View) {
        // 각 부위별 좌측, 우측 이미지 ID 매핑
        val muscleMap = mapOf(
            1 to listOf(R.id.iv_muscle_front_chest), // 대흉근
            2 to listOf(R.id.iv_muscle_front_shoulder_left, R.id.iv_muscle_front_shoulder_right), // 전면삼각근
            3 to listOf(R.id.iv_muscle_front_abs), // 복근
            4 to listOf(R.id.iv_muscle_front_biceps_left, R.id.iv_muscle_front_biceps_right), // 상완이두근
            5 to listOf(R.id.iv_muscle_front_forearm_left, R.id.iv_muscle_front_forearm_right), // 전완근(전면)
            6 to listOf(R.id.iv_muscle_front_quadriceps_left, R.id.iv_muscle_front_quadriceps_right), // 대퇴사두근
            7 to listOf(R.id.iv_muscle_front_tibialis_left, R.id.iv_muscle_front_tibialis_right), // 전경골근
            8 to listOf(R.id.iv_muscle_back_trapezius), // 승모근
            9 to listOf(R.id.iv_muscle_back_rotator_left, R.id.iv_muscle_back_rotator_right), // 회전근개
            10 to listOf(R.id.iv_muscle_back_deltoid_left, R.id.iv_muscle_back_deltoid_right), // 후면삼각근
            11 to listOf(R.id.iv_muscle_back_triceps_left, R.id.iv_muscle_back_triceps_right), // 상완삼두근
            12 to listOf(R.id.iv_muscle_back_forearm_left, R.id.iv_muscle_back_forearm_right), // 전완근(후면)
            13 to listOf(R.id.iv_muscle_back_lats_left,R.id.iv_muscle_back_lats_right), // 광배근
            16 to listOf(R.id.iv_muscle_back_erector_spinae),
            15 to listOf(R.id.iv_muscle_back_gluteus_maximus), // 둔근
            16 to listOf(R.id.iv_muscle_back_hamstring_left, R.id.iv_muscle_back_hamstring_right), // 햄스트링
            17 to listOf(R.id.iv_muscle_back_triceps_lower_leg_left, R.id.iv_muscle_back_triceps_lower_leg_right) // 아킬레스건
        )

        val orangeColor = ContextCompat.getColor(fragmentView.context, R.color.highlight_orange)

        muscleData.forEach { muscleId ->
            muscleMap[muscleId.toInt()]?.forEach { viewId ->
                val imageView = fragmentView.findViewById<ImageView>(viewId)
                imageView?.let {
                    val colorFilter = PorterDuffColorFilter(orangeColor, PorterDuff.Mode.SRC_ATOP)
                    it.colorFilter = colorFilter
                    it.setOnClickListener {
                        Log.d(TAG,"클릭"+muscleId.toString())
                        val dialog = HealthReportDialogFragment().apply {
                            arguments = Bundle().apply {
                                putLong("muscleId", muscleId)
                            }
                        }
                        dialog.show(parentFragmentManager, "HealthReportDialog")
                    }
                }
            }
        }
    }
}