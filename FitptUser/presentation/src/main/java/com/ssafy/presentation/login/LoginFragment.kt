package com.ssafy.presentation.login;

import android.content.Intent
import android.os.Bundle;
import android.util.Log
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.presentation.R;
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.common.MainActivity
import com.ssafy.presentation.databinding.FragmentLoginBinding
import com.ssafy.presentation.login.viewModel.LoginStatus
import com.ssafy.presentation.login.viewModel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::bind,
    R.layout.fragment_login
) {
    private var isClick = false
    private val loginViewModel: LoginViewModel by activityViewModels()
    @Inject
    lateinit var userDataStoreSource: UserDataStoreSource

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        observeLoginState()
    }

    fun initEvent(){
        binding.ivKakaoMove.setOnClickListener {
            if (!isClick) {
                isClick = true // 클릭 방지 활성화
                kakaoLogin()
            }
        }
    }

    private fun kakaoLogin() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            // 카카오톡으로만 로그인 시도
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        isClick = false
                        return@loginWithKakaoTalk
                    }
                    showToast("카카오톡 로그인에 실패했습니다. 다시 시도해주세요.")
                    isClick = false
                } else if (token != null) {
                    lifecycleScope.launch {
                        //Log.d(TAG,"로그인 관련 ${token.accessToken}")
                        userDataStoreSource.saveKakaoAccessToken(token.accessToken)
                        loginViewModel.login(token.accessToken)
                    }
                }
            }
        } else {
            //Log.e(TAG, "카카오톡이 설치되어 있지 않습니다.")
            showToast("카카오톡이 설치되어 있지 않습니다. 앱을 설치한 후 다시 시도해주세요.")
            isClick = false // 클릭 가능 상태로 변경
        }
    }

    private fun observeLoginState() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.loginState.collect { status ->
                when (status) {
                    is LoginStatus.Success -> {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        requireActivity().finishAffinity()
                    }
                    is LoginStatus.Error -> {
                        lifecycleScope.launch {
                            val token = userDataStoreSource.kakaoAccessToken.first()
                            //loginViewModel.updateAccessToken(token ?: "")
                        }
                        val currentDestination = findNavController().currentDestination?.id
                        if (currentDestination == R.id.loginFragment) {
                            findNavController().navigate(R.id.action_loginFragment_to_registerUserInfoFragment)
                        }
                        loginViewModel.resetLoginState()
                        //나중에 지울거임
                        /*val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        requireActivity().finishAffinity()*/
                    }
                    LoginStatus.Idle -> {

                    }
                }
                isClick = false
            }
        }
    }
}