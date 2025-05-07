package com.ssafy.presentation.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentRegisterUserInfoBinding
import com.ssafy.presentation.databinding.FragmentSearchGymBinding


class SearchGymFragment : BaseFragment<FragmentSearchGymBinding>(
    FragmentSearchGymBinding::bind,
    R.layout.fragment_search_gym
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