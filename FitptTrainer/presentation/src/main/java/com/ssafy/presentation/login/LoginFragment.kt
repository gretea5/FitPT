package com.ssafy.presentation.login;

import android.content.Intent
import android.os.Bundle;
import android.view.View;
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ssafy.presentation.R;
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.common.MainActivity
import com.ssafy.presentation.databinding.FragmentLoginBinding
import com.ssafy.presentation.login.viewModel.LoginStatus
import com.ssafy.presentation.login.viewModel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::bind,
    R.layout.fragment_login
) {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        observeLoginState()
    }

    private fun initEvent(){
        binding.apply {
            btnLoginLogin.setOnClickListener {
                val id = etLoginId.text.toString().trim()
                val password = etLoginPw.text.toString().trim()

                if (id.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "아이디와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                loginViewModel.login(id, password)
            }
        }
    }

    private fun observeLoginState() {
        lifecycleScope.launchWhenCreated {
            loginViewModel.loginState.collect { state ->
                when (state) {
                    is LoginStatus.Idle -> {
                        binding.btnLoginLogin.isEnabled = true
                    }
                    is LoginStatus.Success -> {
                        binding.btnLoginLogin.isEnabled = true
                        navigateToMain()
                    }
                    is LoginStatus.Error -> {
                        binding.btnLoginLogin.isEnabled = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(requireContext(), MainActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

        requireActivity().overridePendingTransition(
            R.anim.slide_in_right, R.anim.slide_out_left
        )

        // 현재 액티비티를 포함한 태스크 모두 종료
        requireActivity().finishAffinity()

        // 로그인 상태 초기화
        loginViewModel.resetLoginState()
    }
}