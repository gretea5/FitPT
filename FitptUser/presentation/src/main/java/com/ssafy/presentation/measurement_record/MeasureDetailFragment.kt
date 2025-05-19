package com.ssafy.presentation.measurement_record

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.domain.model.measure.BodyInfoItem
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentMeasureDetailBinding
import com.ssafy.presentation.databinding.FragmentMeasureListBinding
import com.ssafy.presentation.measurement_record.adapter.BodyInfoAdapter
import com.ssafy.presentation.measurement_record.viewModel.MeasureViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.domain.model.measure_record.MeasureRecordItem
import com.ssafy.presentation.measurement_record.viewModel.GetBodyDetailInfoState
import com.ssafy.presentation.measurement_record.viewModel.GetBodyListInfoState
import com.ssafy.presentation.util.CommonUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MeasureDetailFragment"
@AndroidEntryPoint
class MeasureDetailFragment : BaseFragment<FragmentMeasureDetailBinding>(
    FragmentMeasureDetailBinding::bind,
    R.layout.fragment_measure_detail
) {
    private val measureViewModel : MeasureViewModel by activityViewModels()
    @Inject
    lateinit var userDataStoreSource: UserDataStoreSource

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeBodyDetailList()
        initEvent()
        initView()
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        Log.d(TAG,measureViewModel.measureDetailInfo.value.toString())
        measureViewModel.getBodyDetailInfo(measureViewModel.measureDetailInfo.value!!.compositionLogId)
    }

    fun initView(){
        val detail = measureViewModel.measureDetailInfo.value!!
        binding.tvDateTime.text = CommonUtils.formatCreatedAt(detail.createdAt)
        binding.tvWeightValue.text = detail.weight.toString()
        binding.tvWeightDif.text = formatDiff(detail.weightDif,"kg")
        binding.tvSmmDif.text = formatDiff(detail.smmDif,"%")
        binding.tvBfpDif.text = formatDiff(detail.bfpDif,"kg")
        lifecycleScope.launch {
            val user = userDataStoreSource.user.first()
            binding.tvName.text = user!!.memberName
        }
    }

    fun formatDiff(value: Double,expression: String): String {
        return when {
            value > 0 -> "+${String.format("%.1f", value)}${expression}"
            value < 0 -> "${String.format("%.1f", value)}${expression}" // 음수는 자동으로 '-' 붙음
            else -> "0.0${expression}"
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
                                BodyInfoItem("기초대사량", format1(info.bmr) + " kcal", "적절"),
                                BodyInfoItem("단백질", format1(info.protein) + " kg", info.proteinLabel),
                                BodyInfoItem("무기질", format1(info.mineral) + " kg", info.mineralLabel),
                                BodyInfoItem("세포외수분비", format1(info.ecw) + " kg", info.ecwRatioLabel),)

                            val adapter = BodyInfoAdapter(sampleItems) {

                            }
                            binding.rvBodyComposition.adapter = adapter
                            binding.rvBodyComposition.layoutManager = LinearLayoutManager(requireContext())
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