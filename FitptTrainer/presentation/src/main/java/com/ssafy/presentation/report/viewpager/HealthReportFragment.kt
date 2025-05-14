package com.ssafy.presentation.report.viewpager

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.domain.model.report.MuscleGroup
import com.ssafy.domain.model.report.WorkoutNameScoreItem
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

            // 운동 등록
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
                val newId = System.currentTimeMillis()

                reportViewModel.setCurrentWorkoutId(newId)
                healthReportAdapter.addEditItem(newId)
            }

            // 운동 추가 완료
            ibReportHealthWorkoutAdd.setOnClickListener {
                healthReportAdapter.finalizeLastItem()

                // 입력값 ViewModel에 저장
                reportViewModel.addWorkoutReport()

                // 추가 입력 막기
                setAllMuscleClickable(false)
                resetAllMuscleViewsToGray()
                ibReportHealthWorkoutAdd.isVisible = false
                ibReportHealthWorkoutDelete.isVisible = false

                etReportHealthContent.text.clear()
                etReportHealthContent.isEnabled = false

                cvReportWorkoutAddWorkout.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.highlight_orange)
                )
                cvReportWorkoutAddWorkout.isEnabled = true
            }

            // 운동 삭제
            ibReportHealthWorkoutDelete.setOnClickListener {
                setAllMuscleClickable(false)
                resetAllMuscleViewsToGray()
                ibReportHealthWorkoutAdd.isVisible = false
                ibReportHealthWorkoutDelete.isVisible = false

                etReportHealthContent.text.clear()
                etReportHealthContent.isEnabled = false

                cvReportWorkoutAddWorkout.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlight_orange))
                cvReportWorkoutAddWorkout.isEnabled = true

                healthReportAdapter.removeEditModeItem()
            }

            // 운동별 성과 상세 기록 텍스트 감지
            etReportHealthContent.addTextChangedListener {
                reportViewModel.updateDescription(it?.toString() ?: "")
            }
        }
    }

    private fun observeViewModel() {
        reportViewModel.isAddButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.ibReportHealthWorkoutAdd.apply {
                isClickable = isEnabled
                setImageResource(
                    if (isEnabled) R.drawable.ib_report_workout_add_on
                    else R.drawable.ib_report_workout_add_off
                )
            }
        }
    }

    private fun initRecyclerView() {
        val initialList = mutableListOf<WorkoutNameScoreItem>()

        healthReportAdapter = HealthReportAdapter(
            initialList,
            onItemChanged = {
                updateAddButtonState()
            },
            onItemClicked = { selectedId ->
                reportViewModel.setSelectedWorkoutId(selectedId)
            },
        )

        binding.rvReportWorkoutList.adapter = healthReportAdapter
        binding.rvReportWorkoutList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun updateSelectedWorkoutItem(id: Long, isSelected: Boolean) {
        binding.ibReportHealthWorkoutDelete.isVisible = true

        reportViewModel.setSelectedWorkoutId(id)

        reportViewModel.selectedWorkoutItem.observe(viewLifecycleOwner) { item ->
            item?.let {
                binding.apply {
                    if (isSelected) {
                        resetAllMuscleViewsToGray()
                        highlightMuscleGroupsByIds(it.activationMuscleId)

                        tvReportHealthContent.isVisible = true
                        etReportHealthContent.isVisible = false

                        tvReportHealthContent.setText(it.exerciseComment)
                    } else {
                        resetAllMuscleViewsToGray()

                        tvReportHealthContent.isVisible = false
                        etReportHealthContent.isVisible = true

                        tvReportHealthContent.setText("")
                    }
                }
            }
        }
    }

    private fun updateAddButtonState() {
        val lastItem = healthReportAdapter.items.lastOrNull()
        val isValid = lastItem?.isEditing == true &&
                lastItem.name.isNotBlank() &&
                lastItem.score.isNotBlank()

        binding.ibReportHealthWorkoutAdd.apply {
            isEnabled = isValid
            setImageResource(
                if (isValid) R.drawable.ib_report_workout_add_on
                else R.drawable.ib_report_workout_add_off
            )
        }

        // ViewModel에도 반영
        reportViewModel.updateName(lastItem?.name ?: "")
        reportViewModel.updateScore(lastItem?.score ?: "")
    }

    // 근육 초기화 설정
    private fun initMuscles() {
        binding.apply {
            muscleGroups = listOf(
                MuscleGroup(1, "chest", listOf(ivMuscleFrontChest)),
                MuscleGroup(2, "shoulder_front", listOf(ivMuscleFrontShoulderLeft, ivMuscleFrontShoulderRight)),
                MuscleGroup(3, "abs", listOf(ivMuscleFrontAbs)),
                MuscleGroup(4, "biceps", listOf(ivMuscleFrontBicepsLeft, ivMuscleFrontBicepsRight)),
                MuscleGroup(5, "forearm_front", listOf(ivMuscleFrontForearmLeft, ivMuscleFrontForearmRight)),
                MuscleGroup(6, "quariceps", listOf(ivMuscleFrontQuadricepsLeft, ivMuscleFrontQuadricepsRight)),
                MuscleGroup(7, "tilialis", listOf(ivMuscleFrontTibialisLeft, ivMuscleFrontTibialisRight)),

                MuscleGroup(8, "trapezius", listOf(ivMuscleBackTrapezius)),
                MuscleGroup(9, "rotator", listOf(ivMuscleBackRotatorLeft, ivMuscleBackRotatorRight)),
                MuscleGroup(10, "shoulder_back", listOf(ivMuscleBackDeltoidLeft, ivMuscleBackDeltoidRight)),
                MuscleGroup(11, "triceps", listOf(ivMuscleBackTricepsLeft, ivMuscleBackTricepsRight)),
                MuscleGroup(12, "forearm_back", listOf(ivMuscleBackForearmLeft, ivMuscleBackForearmRight)),
                MuscleGroup(13, "lats", listOf(ivMuscleBackLatsLeft, ivMuscleBackLatsRight)),
                MuscleGroup(14, "spinae", listOf(ivMuscleBackErectorSpinae)),
                MuscleGroup(15, "gluteus", listOf(ivMuscleBackGluteusMaximus)),
                MuscleGroup(16, "hamstring", listOf(ivMuscleBackHamstringLeft, ivMuscleBackHamstringRight)),
                MuscleGroup(17, "lower_leg", listOf(ivMuscleBackTricepsLowerLegLeft, ivMuscleBackTricepsLowerLegRight))
            )
        }

        setAllMuscleClickable(false)
    }

    // 선택한 근육 색 변경
    private fun toggleMuscleGroupSelection(group: MuscleGroup) {
        // ViewModel을 통해 상태 업데이트
        reportViewModel.toggleMuscleSelection(group)

        // ViewModel 호출 후의 상태를 반영해 색상 업데이트
        val color = ContextCompat.getColor(
            requireContext(),
            if (group.isSelected) R.color.main_blue else R.color.secondary_gray
        )

        group.imageViews.forEach { iv ->
            val drawable = iv.drawable.mutate()
            drawable.setTint(color)
            iv.setImageDrawable(drawable)
        }
    }

    fun highlightMuscleGroupsByIds(selectedIds: List<Int>) {
        val context = requireContext()
        val grayColor = ContextCompat.getColor(context, R.color.secondary_gray)
        val blueColor = ContextCompat.getColor(context, R.color.main_blue)

        muscleGroups.forEach { group ->
            val isSelected = group.id in selectedIds
            val color = if (isSelected) blueColor else grayColor

            group.imageViews.forEach { iv ->
                val drawable = iv.drawable.mutate()
                drawable.setTint(color)
                iv.setImageDrawable(drawable)
            }

            group.isSelected = isSelected // 상태 반영
        }
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
