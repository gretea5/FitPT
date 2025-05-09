package com.ssafy.presentation.report

import android.os.Bundle
import android.view.View
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentReportEditBinding

class ReportEditFragment : BaseFragment<FragmentReportEditBinding>(
    FragmentReportEditBinding::bind,
    R.layout.fragment_report_edit
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){

    }
}