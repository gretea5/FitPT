package com.ssafy.presentation.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.domain.model.sign.GymInfoItem
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentSearchGymBinding
import com.ssafy.presentation.login.adapter.GymSearchAdapter
import com.ssafy.presentation.login.viewModel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchGymFragment : BaseFragment<FragmentSearchGymBinding>(
    FragmentSearchGymBinding::bind,
    R.layout.fragment_search_gym
) {
    private lateinit var gymSearchAdapter: GymSearchAdapter
    private var selectedGym: GymInfoItem? = null
    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initAdapter()
    }

    private fun initEvent() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnSelect.setOnClickListener {
            selectedGym?.let {
                findNavController().popBackStack()
            }
        }
    }
    private fun initAdapter() {
        val dummyList = listOf(
            GymInfoItem("삼성 체육관", "서울 강남구 삼성동"),
            GymInfoItem("헬스킹짐", "부산 해운대구 마린시티"),
            GymInfoItem("피트니스 월드", "대전 중구 중앙로"),
            GymInfoItem("광주 바디짐", "광주 서구 농성동"),
            GymInfoItem("인천 스포짐", "인천 연수구 송도동")
        )

        gymSearchAdapter = GymSearchAdapter(dummyList) { gymInfoItem ->
            selectedGym = gymInfoItem
            loginViewModel.setGym(gymInfoItem)
            updateNextButtonState(true)
        }
        binding.rvGymSearch.adapter = gymSearchAdapter
        binding.rvGymSearch.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun updateNextButtonState(enabled: Boolean) {
        if (enabled) {
            binding.btnSelect.setBackgroundColor(resources.getColor(R.color.main_orange, null)) // 주황색으로 변경
        } else {
            binding.btnSelect.setBackgroundColor(resources.getColor(R.color.disabled, null)) // 비활성화 색상
        }
    }
}