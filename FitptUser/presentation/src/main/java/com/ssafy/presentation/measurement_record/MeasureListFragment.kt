package com.ssafy.presentation.measurement_record

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.domain.model.measure_record.MeasureRecordItem
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentMeasureListBinding
import com.ssafy.presentation.measurement_record.viewModel.MeasureViewModel
import com.ssafy.presentation.measurement_record.adapter.MeasureListAdapter
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ssafy.domain.model.measure_record.MesureDetail
import com.ssafy.presentation.measurement_record.viewModel.GetBodyListInfoState
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MeasureListFragment : BaseFragment<FragmentMeasureListBinding>(
    FragmentMeasureListBinding::bind,
    R.layout.fragment_measure_list
) {
    private val measureViewModel: MeasureViewModel by activityViewModels()
    private lateinit var adapter: MeasureListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        observeBodyList()
        measureViewModel.getBodyList("createdAt", "desc")
    }

    private fun initAdapter() {
        adapter = MeasureListAdapter(emptyList()) { item, index ->
            val state = measureViewModel.getBodyListInfo.value
            if (state is GetBodyListInfoState.Success) {
                val list = state.getBodyList
                if (index <= list.size - 2 && list.size > 1) {
                    val previous = list[index + 1]
                    val current = list[index]
                    val detail = MesureDetail(
                        bfm = current.bfm,
                        bfp = current.bfp,
                        bmr = current.bmr,
                        bodyAge = current.bodyAge,
                        compositionLogId = current.compositionLogId,
                        createdAt = current.createdAt,
                        ecw = current.ecw,
                        icw = current.icw,
                        memberId = current.memberId,
                        mineral = current.mineral,
                        protein = current.protein,
                        smm = current.smm,
                        weight = current.weight,
                        weightDif = current.weight - previous.weight,
                        bfpDif = current.bfp - previous.bfp,
                        smmDif = current.smm - previous.smm
                    )
                    measureViewModel.setMeasureDetailInfo(detail)
                } else {
                    val current = list[index]
                    val detail = MesureDetail(
                        bfm = current.bfm,
                        bfp = current.bfp,
                        bmr = current.bmr,
                        bodyAge = current.bodyAge,
                        compositionLogId = current.compositionLogId,
                        createdAt = current.createdAt,
                        ecw = current.ecw,
                        icw = current.icw,
                        memberId = current.memberId,
                        mineral = current.mineral,
                        protein = current.protein,
                        smm = current.smm,
                        weight = current.weight,
                        weightDif = 0.0,
                        bfpDif = 0.0,
                        smmDif = 0.0
                    )
                    measureViewModel.setMeasureDetailInfo(detail)
                }
                findNavController().navigate(R.id.action_measure_list_fragment_to_measure_detail_fragment)
            }
        }

        binding.rvMeasureRecord.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMeasureRecord.adapter = adapter
    }

    private fun observeBodyList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                measureViewModel.getBodyListInfo.collect { state ->
                    when (state) {
                        is GetBodyListInfoState.Loading -> {
                        }
                        is GetBodyListInfoState.Success -> {
                            val list = state.getBodyList.map {
                                MeasureRecordItem(it.bfp, it.smm, it.weight, it.createdAt)
                            }
                            adapter.updateList(list)
                        }
                        is GetBodyListInfoState.Error -> {

                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}