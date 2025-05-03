package com.ssafy.presentation.report.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ssafy.presentation.report.viewpager.CoachingFragment
import com.ssafy.presentation.report.viewpager.HealthReportFragment
import com.ssafy.presentation.report.viewpager.MeasureRecordFragment

class ReportVPAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HealthReportFragment()
            1 -> MeasureRecordFragment()
            else -> CoachingFragment()
        }
    }
}