package com.ssafy.presentation.report.viewpager

import android.graphics.Color
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentHealthReportBinding

class HealthReportFragment : BaseFragment<FragmentHealthReportBinding>(
    FragmentHealthReportBinding::bind,
    R.layout.fragment_health_report
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent() {

        binding.apply {
            tgReportMuscles.addOnButtonCheckedListener { group, checkedId, isChecked ->
                val frontButton = binding.btnReportMusclesFront
                val backButton = binding.btnReportMusclesBack

                if (checkedId == R.id.btn_report_muscles_front && isChecked) {
                    frontButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlight_orange))
                    backButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_gray))
                } else if (checkedId == R.id.btn_report_muscles_back && isChecked) {
                    backButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlight_orange))
                    frontButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_gray))
                }
            }

            val muscleViews = listOf(
                listOf(ivMuscleFrontChest) to "chest",
                listOf(ivMuscleFrontShoulderLeft, ivMuscleFrontShoulderRight) to "shoulder",
                listOf(ivMuscleFrontAbs) to "abs",
                listOf(ivMuscleFrontBicepsLeft, ivMuscleFrontBicepsRight) to "biceps",
                listOf(ivMuscleFrontForearmLeft, ivMuscleFrontForearmRight) to "forearm",
                listOf(ivMuscleFrontQuadricepsLeft, ivMuscleFrontQuadricepsRight) to "quariceps",
                listOf(ivMuscleFrontTibialisLeft, ivMuscleFrontTibialisRight) to "tilialis",
            )

            muscleViews.forEach { (viewGroup, tagKey) ->
                viewGroup.forEach { imageView ->
                    imageView.setOnClickListener {
                        toggleMuscleGroupSelection(viewGroup, tagKey)
                    }
                }
            }
        }
    }

    private fun toggleMuscleGroupSelection(imageViews: List<ImageView>, tagKey: String) {
        val mainBlue = ContextCompat.getColor(requireContext(), R.color.main_blue)
        val mainGray = ContextCompat.getColor(requireContext(), R.color.secondary_gray)

        val currentTag = imageViews.first().getTag(R.id.muscle_tag_key) as? String
        val isBlue = currentTag == tagKey

        val newColor = if (isBlue) mainGray else mainBlue
        val newTag = if (isBlue) "gray" else tagKey

        imageViews.forEach { iv ->
            val drawable = iv.drawable.mutate()
            drawable.setTint(newColor)
            iv.setImageDrawable(drawable)
            iv.setTag(R.id.muscle_tag_key, newTag)
        }
    }
}