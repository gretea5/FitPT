package com.ssafy.presentation.mypage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentDeleteUserInfoBinding
import com.ssafy.presentation.databinding.FragmentMypageBinding
import com.ssafy.presentation.login.LoginActivity

class DeleteUserInfoFragment : BaseFragment<FragmentDeleteUserInfoBinding>(
    FragmentDeleteUserInfoBinding::bind,
    R.layout.fragment_delete_user_info
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnDelete.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            requireActivity().finishAffinity()
        }
    }
}