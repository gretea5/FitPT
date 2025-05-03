package com.ssafy.presentation.report

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.domain.model.report.PtReportItem
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentReportListBinding
import com.ssafy.presentation.report.adapter.PtReportAdapter

class ReportListFragment : BaseFragment<FragmentReportListBinding>(
    FragmentReportListBinding::bind,
    R.layout.fragment_report_list
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }

    fun initAdapter(){
        val dummyData = listOf(
            PtReportItem("2025년 02월 28일 오후 06:20", "박장훈"),
            PtReportItem("2025년 03월 01일 오전 10:00", "이수민")
        )
        val adapter = PtReportAdapter(dummyData) { item ->
            findNavController().navigate(R.id.action_report_list_fragment_to_report_detail_fragment)
        }
        binding.rvPtRecord.adapter = adapter
        binding.rvPtRecord.layoutManager = LinearLayoutManager(requireContext())
    }
}