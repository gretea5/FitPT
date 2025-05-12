package com.ssafy.presentation.report.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentBodyCompositionDietBinding

class BodyCompositionDietFragment : BaseFragment<FragmentBodyCompositionDietBinding>(
    FragmentBodyCompositionDietBinding::bind,
    R.layout.fragment_body_composition_diet
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvevt()
    }

    fun initEvevt(){
        binding.apply {

        }
    }
}