package com.ssafy.presentation.measurement_record

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentMeasureDetailBinding
import com.ssafy.presentation.databinding.FragmentMeasureListBinding

class MeasureDetailFragment : BaseFragment<FragmentMeasureDetailBinding>(
    FragmentMeasureDetailBinding::bind,
    R.layout.fragment_measure_detail
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}