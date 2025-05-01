package com.ssafy.presentation.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentDeleteUserInfoBinding
import com.ssafy.presentation.databinding.FragmentMypageBinding

class DeleteUserInfoFragment : BaseFragment<FragmentDeleteUserInfoBinding>(
    FragmentDeleteUserInfoBinding::bind,
    R.layout.fragment_delete_user_info
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}