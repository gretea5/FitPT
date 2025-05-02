package com.ssafy.presentation.measurement_record

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.domain.model.measure.MeasureRecordItem
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentMeasureListBinding
import com.ssafy.presentation.measurement_record.adapter.MeasureListAdapter


class MeasureListFragment : BaseFragment<FragmentMeasureListBinding>(
    FragmentMeasureListBinding::bind,
    R.layout.fragment_measure_list
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }

    fun initAdapter(){
        val measureList = listOf(
            MeasureRecordItem(13.5, 39.48, 79.44),
            MeasureRecordItem(12.3, 37.45, 72.65),
            MeasureRecordItem(14.1, 41.32, 85.67)
        ) // 예시 데이터

        val adapter = MeasureListAdapter(measureList) { item ->
            findNavController().navigate(R.id.action_measure_list_fragment_to_measure_detail_fragment)
        }
        binding.rvMeasureRecord.adapter = adapter
        binding.rvMeasureRecord.layoutManager = LinearLayoutManager(requireContext())
    }
}