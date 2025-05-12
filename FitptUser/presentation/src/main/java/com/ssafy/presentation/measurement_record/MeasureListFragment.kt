package com.ssafy.presentation.measurement_record

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.domain.model.measure.MeasureRecordItem
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentMeasureListBinding
import com.ssafy.presentation.home.viewModel.GymInfoViewModel
import com.ssafy.presentation.measure.viewModel.MeasureViewModel
import com.ssafy.presentation.measurement_record.adapter.MeasureListAdapter
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ssafy.presentation.measure.viewModel.GetBodyListInfoState
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
        measureViewModel.getBodyList("createAt","desc")
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
                            val adapter = MeasureListAdapter(
                                state.getBodyList.map {
                                    MeasureRecordItem(it.bfp, it.bmr, it.weight) // 매핑
                                }
                            ) { item ->
                                //findNavController().navigate(R.id.action_measure_list_fragment_to_measure_detail_fragment)
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