package com.ssafy.presentation.login

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.domain.model.login.Gym
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentSearchGymBinding
import com.ssafy.presentation.home.viewModel.GymInfoState
import com.ssafy.presentation.home.viewModel.GymInfoViewModel
import com.ssafy.presentation.login.adapter.GymSearchAdapter
import com.ssafy.presentation.login.viewModel.LoginViewModel
import com.ssafy.presentation.mypage.viewModel.MypageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


private const val TAG = "SearchGymFragment"
@AndroidEntryPoint
class SearchGymFragment : BaseFragment<FragmentSearchGymBinding>(
    FragmentSearchGymBinding::bind,
    R.layout.fragment_search_gym
) {
    private lateinit var gymSearchAdapter: GymSearchAdapter
    private var selectedGym: Gym? = null
    private lateinit var gymList: MutableList<Gym>

    private val loginViewModel: LoginViewModel by activityViewModels()
    private val mypageViewModel: MypageViewModel by activityViewModels()
    private val gymInfoViewModel : GymInfoViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getGymListData()
        initEvent()
        initAdapter()
    }

    private fun initEvent() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnNext.setOnClickListener {
            selectedGym?.let {
                findNavController().popBackStack()
            }
        }
        binding.etGym.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val query = binding.etGym.text.toString()
                if (query.isNotBlank()) {
                    gymInfoViewModel.getGymList(query)
                    binding.etGym.text.clear()
                }
                true  // 이벤트 소비 완료
            } else {
                false  // 다른 곳으로 이벤트 전달
            }
        }
    }
    private fun initAdapter() {
        gymList = mutableListOf()
        gymSearchAdapter = GymSearchAdapter(gymList) { gym->
            selectedGym = gym
            loginViewModel.setGym(gym)
            mypageViewModel.setGym(gym)
            updateNextButtonState(true)
        }
        binding.rvGymSearch.adapter = gymSearchAdapter
        binding.rvGymSearch.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun updateNextButtonState(enabled: Boolean) {
        if (enabled) {
            binding.btnNext.setBackgroundColor(resources.getColor(R.color.main_orange, null)) // 주황색으로 변경
        } else {
            binding.btnNext.setBackgroundColor(resources.getColor(R.color.disabled, null)) // 비활성화 색상
        }
    }

    fun getGymListData(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                gymInfoViewModel.gymInfo.collect { gymInfo ->
                    Log.d(TAG,gymInfo.toString())
                    if(gymInfo is GymInfoState.Success) {
                        gymList.clear()
                        gymList.addAll(gymInfo.gymList)
                        gymSearchAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}