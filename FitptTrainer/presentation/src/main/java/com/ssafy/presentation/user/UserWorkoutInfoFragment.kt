package com.ssafy.presentation.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentSchedulingBinding
import com.ssafy.presentation.databinding.FragmentUserWorkoutInfoBinding
import com.ssafy.presentation.user.adapter.UserWorkoutInfoMonthAdapter
import com.ssafy.presentation.util.CommonUtils

private const val TAG = "UserWorkoutInfoFragment_FitPT"

class UserWorkoutInfoFragment : BaseFragment<FragmentUserWorkoutInfoBinding>(
    FragmentUserWorkoutInfoBinding::bind,
    R.layout.fragment_user_workout_info
) {
    private lateinit var monthAdapter: UserWorkoutInfoMonthAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSetting()
        initRecyclerView()
        initEvent()
    }

    fun initEvent() {

    }

    fun initRecyclerView() {
        val months = listOf("전체", "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월")

        monthAdapter = UserWorkoutInfoMonthAdapter(requireContext(), months) { selectedMonth ->
            Log.d(TAG, "initRecyclerView: ${selectedMonth}")
            // 여기서 클릭된 월에 따라 동작 처리
        }

        binding.rvUserReportMonth.apply {
            adapter = monthAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    fun initSetting() {
        binding.apply {
            val changeText = CommonUtils.changeTextColor(
                context = requireContext(),
                fullText = "PT 회원 리스트",
                changeText = "PT",
                color = R.color.highlight_orange
            )

            tvUserUserListTitle.text = changeText
        }
    }
}