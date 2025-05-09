package com.ssafy.presentation.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentSchedulingBinding
import com.ssafy.presentation.databinding.FragmentUserWorkoutInfoBinding
import com.ssafy.presentation.util.CommonUtils


class UserWorkoutInfoFragment : BaseFragment<FragmentUserWorkoutInfoBinding>(
    FragmentUserWorkoutInfoBinding::bind,
    R.layout.fragment_user_workout_info
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSetting()
        initEvent()
    }

    fun initEvent(){

    }

    fun initSetting(){
        binding.apply {
            val changeText = CommonUtils.changeTextColor(
                context = requireContext(),
                fullText = "PT 회원 리스트",
                changeText = "PT",
                color = R.color.highlight_orange
            )

            tvUserUserListTitle.text = changeText
        }
    }
}