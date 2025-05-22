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
import com.ssafy.presentation.measurement_record.viewModel.GetBodyDetailInfoState
import com.ssafy.presentation.measurement_record.viewModel.MeasureViewModel
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
    private val measureViewModel : MeasureViewModel by activityViewModels()
    @Inject
    lateinit var userDataStoreSource: UserDataStoreSource

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeBodyDetailList()
        initView()
    }

    fun initView(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                reportViewModel.getReportDetailInfo.collect { state ->
                    when (state) {
                        is ReportDetailState.Success -> {
                            Log.d(TAG, state.reportDetail.toString())
                            measureViewModel.getBodyDetailInfo(state.reportDetail.compositionResponseDto.compositionLogId)
                            binding.tvName.text = userDataStoreSource.user.firstOrNull()?.memberName ?: ""
                            binding.tvWeightValue.text = state.reportDetail.compositionResponseDto.weight.toString()
                            binding.tvDateTime.text = CommonUtils.formatCreatedAt(state.reportDetail.compositionResponseDto.createdAt)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun observeBodyDetailList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                measureViewModel.getBodyDetailInfo.collect { state ->
                    when (state) {
                        is GetBodyDetailInfoState.Loading -> {
                        }

                        is GetBodyDetailInfoState.Success -> {
                            val info = state.getBodydetail

                            // 소수점 한 자리로 포맷
                            fun format1(value: Double): String = String.format("%.1f", value)
                            val sampleItems = listOf(
                                BodyInfoItem("체지방률", format1(info.bfp) + " %", info.bfpLabel),
                                BodyInfoItem("골격근량", format1(info.smm) + " kg", info.smmLabel),
                                BodyInfoItem("체지방량", format1(info.bfm) + " kg", info.bfmLabel),
                                BodyInfoItem("기초대사량", format1(info.bmr) + " kcal", "적정"),
                                BodyInfoItem(
                                    "단백질",
                                    format1(info.protein) + " kg",
                                    info.proteinLabel
                                ),
                                BodyInfoItem(
                                    "무기질",
                                    format1(info.mineral) + " kg",
                                    info.mineralLabel
                                ),
                                BodyInfoItem(
                                    "세포외수분비",
                                    format1(info.ecw) + " kg",
                                    info.ecwRatioLabel
                                ),
                            )

                            val adapter = BodyInfoAdapter(sampleItems) {

                            }
                            binding.rvBodyComposition.adapter = adapter
                            binding.rvBodyComposition.layoutManager =
                                LinearLayoutManager(requireContext())
                        }

                        is GetBodyDetailInfoState.Error -> {

                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}