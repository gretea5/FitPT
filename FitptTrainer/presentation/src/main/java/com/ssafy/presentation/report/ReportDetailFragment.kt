package com.ssafy.presentation.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.FragmentReportDetailBinding

import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentReportEditBinding
import com.ssafy.presentation.databinding.FragmentSchedulingBinding

class ReportDetailFragment : BaseFragment<FragmentReportDetailBinding>(
    FragmentReportDetailBinding::bind,
    R.layout.fragment_report_detail
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){

    }
}