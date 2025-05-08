package com.ssafy.presentation.login;

import android.content.Intent
import android.os.Bundle;
import android.util.Log
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.presentation.R;
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.common.MainActivity
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
        binding.apply {
            btnLoginLogin.setOnClickListener {
                val intent = Intent(requireContext(), MainActivity::class.java)

                // 백스택 모두 제거, 뒤로 가기 방지
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

                requireActivity().overridePendingTransition(
                    R.anim.slide_in_right, R.anim.slide_out_left
                )

                // 현재 액티비티를 포함한 태스크 모두 종료
                requireActivity().finishAffinity()
            }
        }
    }
}