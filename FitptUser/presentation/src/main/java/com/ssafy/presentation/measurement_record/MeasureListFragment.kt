package com.ssafy.presentation.measurement_record

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentEditUserInfoBinding
import com.ssafy.presentation.databinding.FragmentMeasureListBinding


class MeasureListFragment : BaseFragment<FragmentMeasureListBinding>(
    FragmentMeasureListBinding::bind,
    R.layout.fragment_measure_list
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}