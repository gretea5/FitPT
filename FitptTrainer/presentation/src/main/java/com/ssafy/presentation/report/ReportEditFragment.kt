package com.ssafy.presentation.report

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentReportEditBinding
import com.ssafy.presentation.report.adapter.ReportViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import com.ssafy.presentation.report.viewmodel.ReportViewModel

private const val TAG = "ReportEditFragment_FitPT"

@AndroidEntryPoint
class ReportEditFragment : BaseFragment<FragmentReportEditBinding>(
    FragmentReportEditBinding::bind,
    R.layout.fragment_report_edit
) {
    private lateinit var viewPagerAdapter: ReportViewPagerAdapter
    private val viewModel: ReportViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTabLayout()
        initEvent()
    }

    fun initEvent() {
        binding.apply {
            ibReportBack.setOnClickListener {
                findNavController().navigate(R.id.action_reportEditFragment_to_userWorkoutInfoFragment)
            }
        }
    }

    fun initTabLayout() {
        viewPagerAdapter = ReportViewPagerAdapter(requireActivity())
        binding.vpReport.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tlReport, binding.vpReport) { tab, position ->
            val tabText = when (position) {
                0 -> "헬스 보고서"
                1 -> "체성분 기록 / 식단 코칭"
                else -> ""
            }

            val textView = TextView(requireContext()).apply {
                text = tabText
                textSize = 24f
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_semi_bold)
                gravity = Gravity.CENTER

                // 기본 색상 설정
                setTextColor(ContextCompat.getColor(context, R.color.main_gray))
            }

            // 선택된 탭 색상 변경
            tab.customView = textView
            binding.tlReport.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    // 선택된 탭의 텍스트 색상 변경
                    (tab?.customView as? TextView)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.highlight_orange))
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    // 선택되지 않은 탭의 텍스트 색상 변경
                    (tab?.customView as? TextView)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_gray))
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    // 재선택 시 색상 변경
                    (tab?.customView as? TextView)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.highlight_orange))
                }
            })
        }.attach()

    }
}