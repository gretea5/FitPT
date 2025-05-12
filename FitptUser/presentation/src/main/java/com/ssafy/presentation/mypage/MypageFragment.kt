package com.ssafy.presentation.mypage

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MypageFragment : BaseFragment<FragmentMypageBinding>(
    FragmentMypageBinding::bind,
    R.layout.fragment_mypage
) {
    @Inject
    lateinit var userDataStore: UserDataStoreSource


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUserDataStore(userDataStore)
        initEvent()
    }
    fun initEvent(){
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


    fun observeUserDataStore(dataStoreSource: UserDataStoreSource) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                dataStoreSource.user.collect { userInfo ->
                    val koreanAge = CommonUtils.getKoreanAge(userInfo?.memberBirth!!)
                    binding.tvProfileName.text = userInfo?.memberName
                    binding.tvValueAge.text = koreanAge
                    binding.tvValueHeight.text = userInfo?.memberHeight.toString()+"cm"
                    binding.tvValueWeight.text = userInfo?.memberWeight.toString()+"kg"
                    binding.tvValueGym.text = userInfo?.admin.toString()
                    binding.tvValueTrainer.text = userInfo?.trainerId.toString()
                    // 여기서 TextView 업데이트 같은 것도 가능
                }
            }
        }
    }
}