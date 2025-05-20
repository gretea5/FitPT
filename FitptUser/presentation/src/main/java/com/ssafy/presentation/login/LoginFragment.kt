package com.ssafy.presentation.login;

import android.content.Intent
import android.os.Bundle;
import android.os.Handler
import android.os.Looper
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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LoginFragment"
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

    override fun onDestroyView() {
        super.onDestroyView()
        isClick = false
    }
    fun initEvent(){
        binding.ivKakaoMove.setOnClickListener {
            Log.d(TAG,"카카오 버튼을 클릭하였습니다")
            if (!isClick) {
                isClick = true // 클릭 방지 활성화
                Log.d(TAG,"클리이 리셋")
                kakaoLogin()
            }
        }
    }

    private fun kakaoLogin(retryCount: Int = 0) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        isClick = false
                        return@loginWithKakaoTalk
                    }

                    showToast("카카오톡 로그인에 실패했습니다. 다시 시도해주세요.")
                    isClick = false

                    // 최대 3회까지 재시도
                    if (retryCount < 3) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            kakaoLogin(retryCount + 1)
                        }, 1000) // 1초 후 재시도
                    }
                } else if (token != null) {
                    lifecycleScope.launch {
                        Log.d(TAG, "로그인 관련 ${token.accessToken}")
                        userDataStoreSource.saveKakaoAccessToken(token.accessToken)

                        // FCM 토큰 기다리기
                        var fcmToken = userDataStoreSource.fcmToken.firstOrNull()
                        Log.d(TAG, "FCM 토큰: $fcmToken")
                        if (fcmToken == null) {
                            fcmToken = waitForFcmToken()
                        }
                        Log.d(TAG, "FCM 토큰 확보 완료: $fcmToken")
                        // 사용자 정보 저장
                        val userDeferred = CompletableDeferred<Unit>()
                        UserApiClient.instance.me { user, error ->
                            if (error != null) {
                                Log.e(TAG, "사용자 정보 요청 실패", error)
                            } else if (user != null) {
                                val nickname = user.kakaoAccount?.profile?.nickname
                                lifecycleScope.launch {
                                    userDataStoreSource.saveNickname(nickname ?: "")
                                }
                                Log.d(TAG, "사용자 이름: $nickname")
                            }
                            userDeferred.complete(Unit)
                        }
                        userDeferred.await()

                        loginViewModel.login()
                        isClick = false
                    }
                }
            }
        } else {
            showToast("카카오톡이 설치되어 있지 않습니다. 앱을 설치한 후 다시 시도해주세요.")
            isClick = false
        }
    }

    private suspend fun waitForFcmToken(): String {
        var token: String?
        do {
            token = userDataStoreSource.fcmToken.firstOrNull()
            if (token == null) delay(100)
        } while (token == null)
        return token
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