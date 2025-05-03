package com.ssafy.presentation.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentCoachingBinding
import com.ssafy.presentation.databinding.FragmentReportListBinding

class CoachingFragment : BaseFragment<FragmentCoachingBinding>(
    FragmentCoachingBinding::bind,
    R.layout.fragment_coaching
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}