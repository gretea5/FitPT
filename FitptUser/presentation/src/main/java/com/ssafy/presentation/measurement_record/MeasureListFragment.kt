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


private const val TAG = "MeasureListFragment"
@AndroidEntryPoint
class MeasureListFragment : BaseFragment<FragmentMeasureListBinding>(
    FragmentMeasureListBinding::bind,
    R.layout.fragment_measure_list
) {
    private val measureViewModel : MeasureViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeBodyList()
        initAdapter()
        measureViewModel.getBodyList("createdAt","desc")
        Log.d(TAG,measureViewModel.getBodyListInfo.value.toString())
    }

    fun initAdapter(){
        binding.rvMeasureRecord.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeBodyList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                measureViewModel.getBodyListInfo.collect { state ->
                    when (state) {
                        is GetBodyListInfoState.Loading -> {
                            Log.d(TAG, "로딩 중...")
                        }
                        is GetBodyListInfoState.Success -> {
                            Log.d(TAG, "성공: ${state.getBodyList}")
                            // 여기서 어댑터에 데이터 전달 가능
                            Log.d(TAG,state.getBodyList.get(0).toString())
                            Log.d(TAG,state.getBodyList.get(1).toString())
                            val adapter = MeasureListAdapter(
                                state.getBodyList.map {
                                    MeasureRecordItem(it.bfp, it.smm, it.weight,it.createdAt) // 매핑
                                }
                            ) { item, index ->
                                val current = state.getBodyList[index]
                                if (index<=state.getBodyList.size-2&&state.getBodyList.size > 1) {
                                    val previous = state.getBodyList[index+1]

                                    // diff 계산
                                    val weightDif = current.weight - previous.weight
                                    val bfpDif = current.bfp - previous.bfp
                                    val smmDif = current.smm - previous.smm
                                    // MesureDetail 생성
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
                                        weightDif = weightDif,
                                        bfpDif = bfpDif,
                                        smmDif = smmDif
                                    )
                                    // ViewModel에 저장
                                    measureViewModel.setMeasureDetailInfo(detail)
                                }
                                else{
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
                            binding.rvMeasureRecord.adapter = adapter
                        }
                        is GetBodyListInfoState.Error -> {
                            Log.d(TAG, "에러: ${state.message}")
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

}