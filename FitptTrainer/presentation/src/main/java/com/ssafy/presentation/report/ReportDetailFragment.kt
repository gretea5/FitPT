package com.ssafy.presentation.report

import android.os.Bundle
import android.view.View
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.FragmentReportDetailBinding

import com.ssafy.presentation.base.BaseFragment

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