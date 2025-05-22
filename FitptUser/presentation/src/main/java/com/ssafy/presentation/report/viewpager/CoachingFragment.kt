package com.ssafy.presentation.report.viewpager

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentCoachingBinding
import com.ssafy.presentation.report.viewModel.ReportDetailState
import com.ssafy.presentation.report.viewModel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoachingFragment : BaseFragment<FragmentCoachingBinding>(
    FragmentCoachingBinding::bind,
    R.layout.fragment_coaching
) {
    private val reportViewModel : ReportViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                reportViewModel.getReportDetailInfo.collect { state ->
                    when (state) {
                        is ReportDetailState.Success -> {
                            binding.tvCoachging.text = state.reportDetail.reportComment
                        }
                        is ReportDetailState.Loading -> {
                            binding.tvCoachging.text = "로딩 중..."
                        }
                        is ReportDetailState.Error -> {
                            binding.tvCoachging.text = "에러: ${state.message}"
                        }
                        is ReportDetailState.Initial -> {
                            // 초기 상태라면 아무것도 안 해도 됨
                        }
                    }
                }
            }
        }
    }
}