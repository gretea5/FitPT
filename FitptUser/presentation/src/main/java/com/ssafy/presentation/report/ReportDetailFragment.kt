package com.ssafy.presentation.report

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.common.MainActivity
import com.ssafy.presentation.databinding.FragmentReportDetailBinding
import com.ssafy.presentation.report.adapter.PtReportAdapter
import com.ssafy.presentation.report.adapter.ReportVPAdapter
import com.ssafy.presentation.report.viewModel.ReportListState
import com.ssafy.presentation.report.viewModel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import android.util.Log
import com.ssafy.presentation.report.viewModel.ReportDetailState

private const val TAG = "ReportDetailFragment"
@AndroidEntryPoint
class ReportDetailFragment : BaseFragment<FragmentReportDetailBinding>(
    FragmentReportDetailBinding::bind,
    R.layout.fragment_report_detail
) {
    private val reportViewModel : ReportViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTabLayout()
        observeReportDetail()
        initEvent()
    }

    private fun initTabLayout() {
        binding.tabLayout.apply {
            addTab(binding.tabLayout.newTab().setText("헬스 보고서"))
            addTab(binding.tabLayout.newTab().setText("체성분 기록"))
            addTab(binding.tabLayout.newTab().setText("식단 및 코칭"))
        }

        binding.tabVp.apply {
            adapter = ReportVPAdapter(requireActivity() as MainActivity)
            isUserInputEnabled = false
        }

        TabLayoutMediator(binding.tabLayout, binding.tabVp) { tab, position ->
            tab.text = if (position == 0) "헬스 보고서" else if (position == 1) "체성분 기록" else "식단 및 코칭"
        }.attach()


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabTextView = getTextViewFromTab(tab)
                tabTextView?.setTypeface(null, Typeface.NORMAL)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabTextView = getTextViewFromTab(tab)
                tabTextView?.setTypeface(null, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                val tabTextView = getTextViewFromTab(tab)
                tabTextView?.setTypeface(null, Typeface.NORMAL)
            }
        })
    }

    private fun getTextViewFromTab(tab: TabLayout.Tab): TextView? {
        val tabLayout = tab.parent as TabLayout
        val tabStrip = tabLayout.getChildAt(0) as ViewGroup
        val tabView = tabStrip.getChildAt(tab.position) as ViewGroup

        for (i in 0 until tabView.childCount) {
            val child = tabView.getChildAt(i)
            if (child is TextView) {
                return child
            }
        }
        return null
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        reportViewModel.getReportDetail(reportViewModel.selectReport.value)
    }

    private fun observeReportDetail() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                reportViewModel.getReportDetailInfo.collect { state ->
                    when (state) {
                        is ReportDetailState.Loading -> {
                            // 로딩 UI 표시 가능
                        }
                        is ReportDetailState.Success -> {
                            Log.d(TAG,state.reportDetail.toString())
                        }
                        is ReportDetailState.Error -> {

                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}