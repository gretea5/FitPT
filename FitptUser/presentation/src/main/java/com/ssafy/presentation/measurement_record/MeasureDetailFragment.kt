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
import androidx.lifecycle.lifecycleScope
import com.ssafy.data.datasource.UserDataStoreSource
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
        initEvent()
        initView()
        setupRecyclerView()
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        Log.d(TAG,measureViewModel.measureDetailInfo.value.toString())
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

    private fun setupRecyclerView() {
        val sampleItems = listOf(
            BodyInfoItem("체중", measureViewModel.measureDetailInfo.value!!.weight.toString()+" kg", "보통"),
            BodyInfoItem("체지방률", measureViewModel.measureDetailInfo.value!!.smm.toString()+" %", "위험"),
            BodyInfoItem("골격근량", measureViewModel.measureDetailInfo.value!!.bfp.toString()+" kg", "주의"),
            BodyInfoItem("체지방량", measureViewModel.measureDetailInfo.value!!.bfm.toString()+" kg", "주의"),
            BodyInfoItem("기초대사량", measureViewModel.measureDetailInfo.value!!.bmr.toString()+" kg", "주의"),
            BodyInfoItem("체수분", measureViewModel.measureDetailInfo.value!!.icw.toString()+" kg", "주의"),
            BodyInfoItem("단백질", measureViewModel.measureDetailInfo.value!!.protein.toString()+" kg", "주의"),
            BodyInfoItem("무기질", measureViewModel.measureDetailInfo.value!!.mineral.toString()+" kg", "주의"),
            BodyInfoItem("세포외수분비", measureViewModel.measureDetailInfo.value!!.ecw.toString()+" kg", "주의"),
        )

        val adapter = BodyInfoAdapter(sampleItems) {

        }

        binding.rvBodyComposition.adapter = adapter
        binding.rvBodyComposition.layoutManager = LinearLayoutManager(requireContext())
    }


    fun formatDiff(value: Double,expression: String): String {
        return when {
            value > 0 -> "+${String.format("%.1f", value)}${expression}"
            value < 0 -> "${String.format("%.1f", value)}${expression}" // 음수는 자동으로 '-' 붙음
            else -> "0.0${expression}"
        }
    }
}