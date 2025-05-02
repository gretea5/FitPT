package com.ssafy.presentation.measure

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentLoginBinding
import com.ssafy.presentation.databinding.FragmentMeasureBinding

class MeasureFragment : BaseFragment<FragmentMeasureBinding>(
    FragmentMeasureBinding::bind,
    R.layout.fragment_measure
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){
        binding.btnMeasureStart.setOnClickListener {
            findNavController().navigate(R.id.action_measure_fragment_to_measure_progress_fragment)
        }
    }
}