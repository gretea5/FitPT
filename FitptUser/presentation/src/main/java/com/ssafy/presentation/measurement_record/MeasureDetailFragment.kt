package com.ssafy.presentation.measurement_record

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.domain.model.measure.BodyInfoItem
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentMeasureDetailBinding
import com.ssafy.presentation.databinding.FragmentMeasureListBinding
import com.ssafy.presentation.measurement_record.adapter.BodyInfoAdapter

class MeasureDetailFragment : BaseFragment<FragmentMeasureDetailBinding>(
    FragmentMeasureDetailBinding::bind,
    R.layout.fragment_measure_detail
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        setupRecyclerView()
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        val sampleItems = listOf(
            BodyInfoItem("체중", "80.1 kg", "보통"),
            BodyInfoItem("골격근량", "34.2 kg", "주의"),
            BodyInfoItem("체지방률", "25.5 %", "위험")
        )

        val adapter = BodyInfoAdapter(sampleItems) {

        }

        binding.rvBodyComposition.adapter = adapter
        binding.rvBodyComposition.layoutManager = LinearLayoutManager(requireContext())
    }
}