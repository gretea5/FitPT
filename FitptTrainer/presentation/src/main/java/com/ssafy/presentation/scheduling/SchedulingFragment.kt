package com.ssafy.presentation.scheduling

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentLoginBinding
import com.ssafy.presentation.databinding.FragmentSchedulingBinding

class SchedulingFragment : BaseFragment<FragmentSchedulingBinding>(
    FragmentSchedulingBinding::bind,
    R.layout.fragment_scheduling
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){

    }
}