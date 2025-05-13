package com.ssafy.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.View
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "HomeFragment_FitPT"

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind,
    R.layout.fragment_home
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        Log.d(TAG, "onViewCreated: ")
    }

    fun initEvent() {

    }
}