package com.ssafy.presentation.mypage

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.common.MainActivity
import com.ssafy.presentation.databinding.FragmentEditUserInfoBinding
import com.ssafy.presentation.databinding.FragmentMypageBinding
import com.ssafy.presentation.databinding.PopupGenderMenuBinding
import com.ssafy.presentation.home.viewModel.GymInfoViewModel
import com.ssafy.presentation.home.viewModel.UserInfoViewModel
import com.ssafy.presentation.login.viewModel.LoginViewModel
import com.ssafy.presentation.mypage.viewModel.MypageViewModel
import com.ssafy.presentation.util.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

private const val TAG = "EditUserInfoFragment"
@AndroidEntryPoint
class EditUserInfoFragment : BaseFragment<FragmentEditUserInfoBinding>(
    FragmentEditUserInfoBinding::bind,
    R.layout.fragment_edit_user_info
) {
    private val userInfoViewModel : UserInfoViewModel by activityViewModels()
    private val gymInfoViewModel : GymInfoViewModel by activityViewModels()
    private var popupWindow: PopupWindow? = null
    @Inject
    lateinit var userDataStoreSource: UserDataStoreSource

    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
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
        super.onDestroy()
        userInfoViewModel.resetTemporaryUserInfo()
        gymInfoViewModel.tempGymClear()
    }

    fun initView(){
        Log.d(TAG,"실행")
        lifecycleScope.launch {
            // 이미 값이 있는지 확인
            val tempUserInfo = userInfoViewModel.temporaryUserInfo.first()
            Log.d(TAG,tempUserInfo.toString())
            if (tempUserInfo != null) {
                applyUserInfoToUI(tempUserInfo)
            } else {
                // 처음 진입했을 때만 DataStore에서 가져옴
                val user = userDataStoreSource.user.first()
                user?.let {
                    userInfoViewModel.setTemporaryUserInfo(it) // 초기화
                    Log.d(TAG,"보여주세요"+userInfoViewModel.temporaryUserInfo.value.toString())
                    applyUserInfoToUI(it)
                }
            }
        }
        if(gymInfoViewModel.tempgymInfo.value!=null){
            binding.tvGym.setText(gymInfoViewModel.tempgymInfo.value!!.gymName)
        }
    }

    fun initEvent(){
        binding.btnNext.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val memberId = userDataStoreSource.user.first()!!.memberId
                val userInfo = UserInfo(
                    memberId = memberId,
                    admin = gymInfoViewModel.tempgymInfo.value!!.adminId,
                    memberName = userInfoViewModel.temporaryUserInfo.value!!.memberName,
                    memberGender = userInfoViewModel.temporaryUserInfo.value!!.memberGender,
                    memberHeight = userInfoViewModel.temporaryUserInfo.value!!.memberHeight,
                    memberWeight = userInfoViewModel.temporaryUserInfo.value!!.memberWeight,
                    memberBirth = userInfoViewModel.temporaryUserInfo.value!!.memberBirth,
                    trainerId = userInfoViewModel.temporaryUserInfo.value!!.trainerId
                )
                // 데이터 업데이트
                userInfoViewModel.updateUser(userInfo)

                userDataStoreSource.saveUser(userInfo)  // 데이터 저장 시작
                userDataStoreSource.user.collect { user ->  // 저장된 데이터 가져오기
                    Log.d(TAG, "UserInfo from DataStore: $user")
                    // 데이터가 갱신되었으면, 그 후에 화면 전환
                    findNavController().popBackStack()
                }
            }
        }
        binding.cvGym.setOnClickListener {
            findNavController().navigate(R.id.action_edit_user_info_fragment_to_searchGymFragment2)
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
            //성별 변경
            binding.layoutGender.setBackgroundResource(R.drawable.bg_card_border_active)
            updateButtonState()
            popupWindow?.dismiss()
            userInfoViewModel.updateGender(genderTitle)
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
        if(binding.tvGender.text!=""){
            binding.layoutGender.setBackgroundResource(R.drawable.bg_card_border_active)
        }
        if(binding.tvGym.text!=""){
            binding.layoutGym.setBackgroundResource(R.drawable.bg_card_border_active)
        }
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
            userInfoViewModel.updateHeight(binding.etHeight.text.toString().toDouble())
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
            userInfoViewModel.updateWeight(binding.etWeight.text.toString().toDouble())
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
            val formattedDate = CommonUtils.formatBirthDate(birthStr)
            // 디버깅 로그 추가
            Log.d(TAG, "formattedDate: $formattedDate")

            // 유효한 날짜만 업데이트
            if (formattedDate != null && formattedDate.isNotEmpty()) {
                userInfoViewModel.updateBirth(formattedDate)
            } else {
                Log.d(TAG, "Invalid birth date format.")
            }
            userInfoViewModel.updateBirth(formattedDate?:"")
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
            binding.btnNext.isEnabled = true
        } else {
            binding.btnNext.setBackgroundColor(resources.getColor(R.color.disabled, null))
            binding.btnNext.isActivated = false
            binding.btnNext.isEnabled = false
        }
    }

    private fun applyUserInfoToUI(user: UserInfo) {
        val birth = user.memberBirth ?: ""
        val formatted = CommonUtils.formatBirthToYYYYMMDD(birth)
        binding.etBirth.setText(formatted)
        binding.etWeight.setText(user.memberWeight?.toInt()?.toString() ?: "")
        binding.etHeight.setText(user.memberHeight?.toInt()?.toString() ?: "")
        binding.tvGender.text = if (user.memberGender == "남성") "남성" else "여성"
    }
}