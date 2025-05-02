package com.ssafy.presentation.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentMypageBinding
import com.ssafy.presentation.databinding.FragmentNotificationBinding

class MypageFragment : BaseFragment<FragmentMypageBinding>(
    FragmentMypageBinding::bind,
    R.layout.fragment_mypage
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
}