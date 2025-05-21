package com.ssafy.presentation.report.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ssafy.presentation.report.viewpager.BodyCompositionDietFragment
import com.ssafy.presentation.report.viewpager.HealthReportFragment

class ReportViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HealthReportFragment()
            1 -> BodyCompositionDietFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}