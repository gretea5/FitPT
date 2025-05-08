package com.ssafy.presentation.login;

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::bind,
    R.layout.fragment_login
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()

    }

    fun initEvent(){
        binding.cvKakaoLogin.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerUserInfoFragment)
        }
    }
}