package com.ssafy.presentation.report.viewpager

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.domain.model.report.MuscleGroup
import com.ssafy.domain.model.report.WorkoutItem
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentHealthReportBinding
import com.ssafy.presentation.report.adapter.HealthReportAdapter
import com.ssafy.presentation.report.viewmodel.HealthReportViewModel

private const val TAG = "HealthReportFragment_FitPT"

class HealthReportFragment : BaseFragment<FragmentHealthReportBinding>(
    FragmentHealthReportBinding::bind,
    R.layout.fragment_health_report
) {
    private lateinit var healthReportAdapter: HealthReportAdapter
    private lateinit var muscleGroups: List<MuscleGroup>
    private val reportViewModel: HealthReportViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMuscles()
        initRecyclerView()
        initEvent()
        observeViewModel()
    }

    private fun initEvent() {
        binding.apply {
            tgReportMuscles.addOnButtonCheckedListener { group, checkedId, isChecked ->
                val frontButton = binding.btnReportMusclesFront
                val backButton = binding.btnReportMusclesBack

                if (checkedId == R.id.btn_report_muscles_front && isChecked) {
                    frontButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlight_orange))
                    backButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_gray))
                    clReportHealthMusclesFront.isVisible = true
                    clReportHealthMusclesBack.isVisible = false

                } else if (checkedId == R.id.btn_report_muscles_back && isChecked) {
                    backButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlight_orange))
                    frontButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_gray))
                    clReportHealthMusclesFront.isVisible = false
                    clReportHealthMusclesBack.isVisible = true
                }
            }

            muscleGroups.forEach { group ->
                group.imageViews.forEach { imageView ->
                    imageView.setOnClickListener {
                        toggleMuscleGroupSelection(group)
                    }
                }
            }

            // 운동 추가
            cvReportWorkoutAddWorkout.setOnClickListener {
                setAllMuscleClickable(true)
                resetAllMuscleViewsToGray()
                ibReportHealthWorkoutAdd.isVisible = true
                ibReportHealthWorkoutDelete.isVisible = true

                etReportHealthContent.text.clear()
                etReportHealthContent.isEnabled = true

                cvReportWorkoutAddWorkout.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondary_gray))
                cvReportWorkoutAddWorkout.isEnabled = false

                // Edit 모드 아이템 추가
                healthReportAdapter.addEditItem()
            }

            ibReportHealthWorkoutAdd.setOnClickListener {
                healthReportAdapter.finalizeLastItem()

                // 추가 입력 막기
                setAllMuscleClickable(false)
                resetAllMuscleViewsToGray()
                ibReportHealthWorkoutAdd.isVisible = false
                ibReportHealthWorkoutDelete.isVisible = false

                etReportHealthContent.text.clear()
                etReportHealthContent.isEnabled = false

                cvReportWorkoutAddWorkout.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlight_orange))
                cvReportWorkoutAddWorkout.isEnabled = true

                // ViewModel 입력값 초기화
                reportViewModel.clearInputs()
            }

            ibReportHealthWorkoutDelete.setOnClickListener {
                setAllMuscleClickable(false)
                resetAllMuscleViewsToGray()
                ibReportHealthWorkoutAdd.isVisible = false
                ibReportHealthWorkoutDelete.isVisible = false

                etReportHealthContent.text.clear()
                etReportHealthContent.isEnabled = false

                cvReportWorkoutAddWorkout.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlight_orange))
                cvReportWorkoutAddWorkout.isEnabled = true
            }
        }
    }

    // 근육 초기화 설정
    private fun initMuscles() {
        binding.apply {
            muscleGroups = listOf(
                MuscleGroup("chest", listOf(ivMuscleFrontChest)),
                MuscleGroup("shoulder_front", listOf(ivMuscleFrontShoulderLeft, ivMuscleFrontShoulderRight)),
                MuscleGroup("abs", listOf(ivMuscleFrontAbs)),
                MuscleGroup("biceps", listOf(ivMuscleFrontBicepsLeft, ivMuscleFrontBicepsRight)),
                MuscleGroup("forearm_front", listOf(ivMuscleFrontForearmLeft, ivMuscleFrontForearmRight)),
                MuscleGroup("quariceps", listOf(ivMuscleFrontQuadricepsLeft, ivMuscleFrontQuadricepsRight)),
                MuscleGroup("tilialis", listOf(ivMuscleFrontTibialisLeft, ivMuscleFrontTibialisRight)),

                MuscleGroup("trapezius", listOf(ivMuscleBackTrapezius)),
                MuscleGroup("rotator", listOf(ivMuscleBackRotatorLeft, ivMuscleBackRotatorRight)),
                MuscleGroup("lats", listOf(ivMuscleBackLatsLeft, ivMuscleBackLatsRight)),
                MuscleGroup("spinae", listOf(ivMuscleBackErectorSpinae)),
                MuscleGroup("shoulder_back", listOf(ivMuscleBackDeltoidLeft, ivMuscleBackDeltoidRight)),
                MuscleGroup("triceps", listOf(ivMuscleBackTricepsLeft, ivMuscleBackTricepsRight)),
                MuscleGroup("forearm_back", listOf(ivMuscleBackForearmLeft, ivMuscleBackForearmRight)),
                MuscleGroup("gluteus", listOf(ivMuscleBackGluteusMaximus)),
                MuscleGroup("hamstring", listOf(ivMuscleBackHamstringLeft, ivMuscleBackHamstringRight)),
                MuscleGroup("lower_leg", listOf(ivMuscleBackTricepsLowerLegLeft, ivMuscleBackTricepsLowerLegRight))
            )
        }

        setAllMuscleClickable(false)
    }

    // 선택한 근육 색 변경
    private fun toggleMuscleGroupSelection(group: MuscleGroup) {
        val color = ContextCompat.getColor(
            requireContext(),
            if (group.isSelected) R.color.secondary_gray else R.color.main_blue
        )

        group.imageViews.forEach { iv ->
            val drawable = iv.drawable.mutate()
            drawable.setTint(color)
            iv.setImageDrawable(drawable)
        }

        group.isSelected = !group.isSelected

        Log.d(TAG, "toggleMuscleGroupSelection: ${group.key}")
    }

    // 모든 근육 회색으로 변경
    fun resetAllMuscleViewsToGray() {
        val grayColor = ContextCompat.getColor(requireContext(), R.color.secondary_gray)

        muscleGroups.forEach { group ->
            group.imageViews.forEach { iv ->
                val drawable = iv.drawable.mutate()
                drawable.setTint(grayColor)
                iv.setImageDrawable(drawable)
            }
            group.isSelected = false
            Log.d(TAG, "resetAllMuscleViewsToGray: ${group.key}")
        }
    }

    // 근육 선택 가능 여부 설정
    fun setAllMuscleClickable(flag: Boolean) {
        muscleGroups.forEach { group ->
            group.imageViews.forEach { iv ->
                iv.isEnabled = flag
            }
        }
    }
}
