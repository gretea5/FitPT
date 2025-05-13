package com.ssafy.presentation.report

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.domain.model.report.PtReportItem
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentReportListBinding
import com.ssafy.presentation.report.adapter.PtReportAdapter
import com.ssafy.presentation.report.viewModel.ReportListState
import com.ssafy.presentation.report.viewModel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "ReportListFragment"
@AndroidEntryPoint
class ReportListFragment : BaseFragment<FragmentReportListBinding>(
    FragmentReportListBinding::bind,
    R.layout.fragment_report_list
) {
    private val reportViewModel : ReportViewModel by activityViewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeReportList()
        initEvent()
    }

    fun initEvent(){
        reportViewModel.getReportList()
    }

    private fun observeReportList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                reportViewModel.getReportListInfo.collect { state ->
                    when (state) {
                        is ReportListState.Loading -> {
                            // 로딩 UI 표시 가능
                        }
                        is ReportListState.Success -> {
                            val adapter = PtReportAdapter(state.reportList) { item ->
                                findNavController().navigate(R.id.action_report_list_fragment_to_report_detail_fragment)
                            }
                            binding.rvPtRecord.adapter = adapter
                            binding.rvPtRecord.layoutManager = LinearLayoutManager(requireContext())
                        }
                        is ReportListState.Error -> {
                            
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}

