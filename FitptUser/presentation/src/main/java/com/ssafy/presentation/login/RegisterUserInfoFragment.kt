package com.ssafy.presentation.login;

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle;
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ssafy.domain.model.login.UserInfo

import com.ssafy.presentation.R;
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.common.MainActivity
import com.ssafy.presentation.databinding.FragmentLoginBinding
import com.ssafy.presentation.databinding.FragmentRegisterUserInfoBinding
import com.ssafy.presentation.databinding.PopupGenderMenuBinding
import com.ssafy.presentation.login.viewModel.LoginViewModel
import com.ssafy.presentation.util.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

private const val TAG = "RegisterUserInfoFragmen"
@AndroidEntryPoint
class RegisterUserInfoFragment : BaseFragment<FragmentRegisterUserInfoBinding>(
    FragmentRegisterUserInfoBinding::bind,
    R.layout.fragment_register_user_info
) {
    private val loginViewModel: LoginViewModel by activityViewModels()
    private var popupWindow: PopupWindow? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeViewModel()
        initEvent()
        initValidation()
        updateButtonState()
    }

    override fun onDestroyView() {
        popupWindow?.dismiss()
        popupWindow = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        loginViewModel.resetClear()
        super.onDestroy()
    }
    fun initView(){
        if(loginViewModel.selectedGym.value!=null){
            binding.tvGym.text = loginViewModel.selectedGym.value!!.gymName
            binding.layoutGym.setBackgroundResource(R.drawable.bg_card_border_active)
        }
        else{
            binding.layoutGym.setBackgroundResource(R.drawable.bg_card_border_inactive)
        }
        if(loginViewModel.userJoin.value.memberGender!=""){
            binding.tvGender.text = loginViewModel.userJoin.value.memberGender
            binding.layoutGender.setBackgroundResource(R.drawable.bg_card_border_active)
        }
        else{
            binding.layoutGender.setBackgroundResource(R.drawable.bg_card_border_inactive)
        }
    }

    fun initEvent(){
        binding.btnNext.setOnClickListener {
            Log.d(TAG,binding.etWeight.text.toString())
            Log.d(TAG,binding.etHeight.text.toString())
            Log.d(TAG,CommonUtils.formatBirthDate(binding.etBirth.text.toString())!!)
            Log.d(TAG,binding.tvGender.text.toString())

            loginViewModel.signUpUser(
                UserInfo(
                    memberId = 0,
                    memberName = "",
                    admin = loginViewModel.selectedGym.value!!.adminId,
                    memberWeight = binding.etWeight.text.toString().toDouble(),
                    memberHeight = binding.etHeight.text.toString().toDouble(),
                    memberBirth = CommonUtils.formatBirthDate(binding.etBirth.text.toString())!!,
                    memberGender = binding.tvGender.text.toString()
                )
            )
        }
        binding.cvGym.setOnClickListener {
            findNavController().navigate(R.id.action_registerUserInfoFragment_to_searchGymFragment)
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.cvGender.setOnClickListener {
            // 키보드 내리기
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etBirth.windowToken, 0)
            showPopupWindow(it)
        }
    }

    private fun showPopupWindow(view: View) {
        // 이미 열려있으면 닫고 반환
        if (popupWindow != null && popupWindow?.isShowing == true) {
            popupWindow?.dismiss()
            return
        }

        // 새 팝업 생성
        val inflater = LayoutInflater.from(requireContext())
        val popupBinding = PopupGenderMenuBinding.inflate(inflater)

        // 새 PopupWindow 인스턴스 생성
        popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true  // focusable을 true로 변경
        )

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        popupWindow?.width = (displayMetrics.widthPixels * 0.85).toInt()
        popupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow?.isOutsideTouchable = true
        popupWindow?.isTouchable = true

        // 팝업 표시
        popupWindow?.showAsDropDown(view)

        val clickListener = View.OnClickListener { clickedView ->
            val genderTitle = when (clickedView.id) {
                R.id.popupItemMale-> "남성"
                R.id.popupItemFemale -> "여성"
                else -> return@OnClickListener
            }
            binding.tvGender.text = genderTitle
            loginViewModel.updateGender(genderTitle)
            binding.layoutGender.setBackgroundResource(R.drawable.bg_card_border_active)
            updateButtonState()
            popupWindow?.dismiss()
        }
        popupBinding.popupItemMale.setOnClickListener(clickListener)
        popupBinding.popupItemFemale.setOnClickListener(clickListener)

        // 팝업이 닫힐 때 참조 정리
        popupWindow?.setOnDismissListener {
            popupWindow = null
        }
    }

    private fun initValidation() {
        binding.etHeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateHeight()
                updateButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etBirth.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateBirth()
                updateButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateWeight()
                updateButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun validateAllInputs(): Boolean {
        val isHeightValid = validateHeight()
        val isBirthValid = validateBirth()
        val isWeightValid = validateWeight()
        val isGymSelected = binding.tvGym.text.isNotBlank()
        val isGenderSelected = binding.tvGender.text.isNotBlank()
        return isHeightValid && isBirthValid && isWeightValid && isGymSelected && isGenderSelected
    }

    private fun validateHeight(): Boolean {
        val heightStr = binding.etHeight.text.toString()
        val height = heightStr.toIntOrNull()
        if (heightStr.isEmpty()) {
            binding.tvHeightError.visibility = View.GONE
            binding.layoutHeight.setBackgroundResource(R.drawable.bg_card_border_inactive)
            return false
        }
        return if (height != null && height !in 100..250) {
            binding.tvHeightError.text = "키는 100~250cm 사이여야 합니다"
            binding.tvHeightError.visibility = View.VISIBLE
            binding.layoutHeight.setBackgroundResource(R.drawable.bg_card_border_inactive)
            false
        } else {
            binding.tvHeightError.visibility = View.GONE
            binding.layoutHeight.setBackgroundResource(R.drawable.bg_card_border_active)
            true
        }
    }

    private fun validateWeight(): Boolean {
        val weightStr = binding.etWeight.text.toString()
        val weight = weightStr.toIntOrNull()
        if (weightStr.isEmpty()) {
            binding.tvWeightError.visibility = View.GONE
            binding.layoutWeight.setBackgroundResource(R.drawable.bg_card_border_inactive)
            return false
        }
        return if (weight != null && weight !in 30..200) {
            binding.tvWeightError.text = "몸무게는 30~400kg 사이여야 합니다"
            binding.tvWeightError.visibility = View.VISIBLE
            binding.layoutWeight.setBackgroundResource(R.drawable.bg_card_border_inactive)
            false
        } else {
            binding.tvWeightError.visibility = View.GONE
            binding.layoutWeight.setBackgroundResource(R.drawable.bg_card_border_active)
            true
        }
    }

    private fun validateBirth(): Boolean {
        val birthStr = binding.etBirth.text.toString()
        if (birthStr.isEmpty()) {
            binding.tvBirthError.visibility = View.GONE
            binding.layoutBirthyear.setBackgroundResource(R.drawable.bg_card_border_inactive)
            return false
        }

        if (!birthStr.matches(Regex("^\\d{8}\$"))) {
            binding.tvBirthError.text = "생년월일은 8자리 숫자(yyyyMMdd)여야 합니다"
            binding.tvBirthError.visibility = View.VISIBLE
            binding.layoutBirthyear.setBackgroundResource(R.drawable.bg_card_border_inactive)
            return false
        }

        try {
            val year = birthStr.substring(0, 4).toInt()
            val month = birthStr.substring(4, 6).toInt()
            val day = birthStr.substring(6, 8).toInt()

            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            if (year !in 1900..currentYear) throw Exception()
            if (month !in 1..12) throw Exception()

            val daysInMonth = arrayOf(31, if (isLeapYear(year)) 29 else 28, 31, 30, 31, 30,
                31, 31, 30, 31, 30, 31)
            if (day !in 1..daysInMonth[month - 1]) throw Exception()

            binding.tvBirthError.visibility = View.GONE
            binding.layoutBirthyear.setBackgroundResource(R.drawable.bg_card_border_active)
            return true
        } catch (e: Exception) {
            binding.tvBirthError.text = "올바른 생년월일을 입력해주세요"
            binding.tvBirthError.visibility = View.VISIBLE
            binding.layoutBirthyear.setBackgroundResource(R.drawable.bg_card_border_inactive)
            return false
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    private fun updateButtonState() {
        if (validateAllInputs()) {
            binding.btnNext.setBackgroundColor(resources.getColor(R.color.main_orange, null))
            binding.btnNext.isActivated = true
        } else {
            binding.btnNext.setBackgroundColor(resources.getColor(R.color.disabled, null))
            binding.btnNext.isActivated = false
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.signUpSuccess.collect { success ->
                when (success) {
                    true -> {
                        // 회원가입 성공 시 화면 이동
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        requireActivity().finishAffinity()
                    }

                    false -> {
                        // 실패 시 토스트만 띄우기
                        showToast("회원가입에 실패했습니다. 다시 시도해주세요.")
                    }

                    null -> {
                        // 초기 상태: 아무 처리하지 않음
                    }
                }
            }
        }
    }
}