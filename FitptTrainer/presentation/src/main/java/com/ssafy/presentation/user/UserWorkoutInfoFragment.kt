package com.ssafy.presentation.user

import android.os.Bundle
import android.view.View
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentUserWorkoutInfoBinding


class UserWorkoutInfoFragment : BaseFragment<FragmentUserWorkoutInfoBinding>(
    FragmentUserWorkoutInfoBinding::bind,
    R.layout.fragment_user_workout_info
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){

    }
}