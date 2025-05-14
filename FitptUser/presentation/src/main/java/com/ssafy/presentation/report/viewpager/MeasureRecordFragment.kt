package com.ssafy.presentation.report.viewpager

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.domain.model.measure.BodyInfoItem
import com.ssafy.domain.model.report.CompositionResponseDto
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentHealthReportBinding
import com.ssafy.presentation.databinding.FragmentMeasureBinding
import com.ssafy.presentation.databinding.FragmentMeasureRecordBinding
import com.ssafy.presentation.measurement_record.adapter.BodyInfoAdapter
import com.ssafy.presentation.report.viewModel.ReportDetailState
import com.ssafy.presentation.report.viewModel.ReportViewModel
import com.ssafy.presentation.util.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MeasureRecordFragment"
@AndroidEntryPoint
class MeasureRecordFragment : BaseFragment< FragmentMeasureRecordBinding>(
    FragmentMeasureRecordBinding::bind,
    R.layout.fragment_measure_record
) {
    private val reportViewModel : ReportViewModel by activityViewModels()
    @Inject
    lateinit var userDataStoreSource: UserDataStoreSource

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        initView()
    }

    private lateinit var bodyInfoAdapter: BodyInfoAdapter

    private fun setupRecyclerView() {
        bodyInfoAdapter = BodyInfoAdapter(emptyList()) {}
        binding.rvBodyComposition.adapter = bodyInfoAdapter
        binding.rvBodyComposition.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun mapToBodyInfoItems(composition: CompositionResponseDto): List<BodyInfoItem> {
        return listOf(
            BodyInfoItem("체중", "${composition.weight} kg", "보통"),
            BodyInfoItem("체지방률", "${composition.bfp} %", "위험"),
            BodyInfoItem("골격근량", "${composition.smm} kg", "주의"),
            BodyInfoItem("체지방량", "${composition.bfm} kg", "주의"),
            BodyInfoItem("기초대사량", "${composition.bmr} kg", "주의"),
            BodyInfoItem("체수분", "${composition.icw} kg", "주의"),
            BodyInfoItem("단백질", "${composition.protein} kg", "주의"),
            BodyInfoItem("무기질", "${composition.mineral} kg", "주의"),
            BodyInfoItem("세포외수분비", "${composition.ecw} kg", "주의"),
        )
    }

    fun initView(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                reportViewModel.getReportDetailInfo.collect { state ->
                    when (state) {
                        is ReportDetailState.Success -> {
                            Log.d(TAG, state.reportDetail.toString())
                            binding.tvName.text = userDataStoreSource.user.firstOrNull()?.memberName ?: ""
                            binding.tvWeightValue.text = state.reportDetail.compositionResponseDto.weight.toString()
                            binding.tvDateTime.text = CommonUtils.formatCreatedAt(state.reportDetail.compositionResponseDto.createdAt)
                            val newItems = mapToBodyInfoItems(state.reportDetail.compositionResponseDto)
                            bodyInfoAdapter.updateItems(newItems)
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}