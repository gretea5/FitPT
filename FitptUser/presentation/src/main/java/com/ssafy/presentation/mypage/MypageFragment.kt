package com.ssafy.presentation.mypage

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentMypageBinding
import com.ssafy.presentation.databinding.FragmentNotificationBinding
import com.ssafy.presentation.util.CommonUtils
import com.ssafy.presentation.util.ToastType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@AndroidEntryPoint
class MypageFragment : BaseFragment<FragmentMypageBinding>(
    FragmentMypageBinding::bind,
    R.layout.fragment_mypage
) {
    @Inject
    lateinit var userDataStore: UserDataStoreSource
    private var backPressedTime: Long = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initView()
    }
    fun initEvent(){
        backEvent()
        binding.ivEdit.setOnClickListener {
            findNavController().navigate(R.id.action_mypage_fragment_to_edit_user_info_fragment)
        }
        binding.clDeleteAccount.setOnClickListener {
            findNavController().navigate(R.id.action_mypage_fragment_to_delete_user_info_fragment)
        }
        binding.clLogout.setOnClickListener {
            val dialogFragment = LogoutDialogFragment()
            dialogFragment.show(parentFragmentManager, "logout_dialog")
        }
    }


    fun initView() {
        lifecycleScope.launch {
            val user = userDataStore.user.first() // 최초 값 한 번만 가져오기
            //Log.d("hello","안녕")
            Log.d("hello",user.toString())
            user?.let {
                val koreanAge = CommonUtils.getKoreanAge(it?.memberBirth!!)
                binding.tvProfileName.text = it?.memberName  // nickname을 TextView에 설정
                binding.tvValueAge.text = koreanAge
                binding.tvValueHeight.text = it?.memberHeight.toString()+"cm"
                binding.tvValueWeight.text = it?.memberWeight.toString()+"kg"
                binding.tvValueGym.text = it?.gymName
                binding.tvValueTrainer.text = it?.trainerName ?: ""
            }
        }
    }


    fun backEvent(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime < 2000) {
                    requireActivity().finish() // 액티비티 종료
                } else {
                    backPressedTime = System.currentTimeMillis()
                    CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.DEFAULT, "한 번 더 누르면 종료됩니다.")
                }
            }
        })
    }
}