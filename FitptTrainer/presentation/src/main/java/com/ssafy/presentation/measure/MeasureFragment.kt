package com.ssafy.presentation.measure

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentMeasureBinding
import com.ssafy.presentation.databinding.FragmentReportEditBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MeasureFragment : BaseFragment<FragmentMeasureBinding>(
    FragmentMeasureBinding::bind,
    R.layout.fragment_measure
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){

    }
}