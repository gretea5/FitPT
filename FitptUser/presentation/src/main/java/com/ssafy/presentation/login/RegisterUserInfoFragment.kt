package com.ssafy.presentation.login;

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.common.MainActivity
import com.ssafy.presentation.databinding.FragmentRegisterUserInfoBinding
import com.ssafy.presentation.util.CommonUtils
import java.util.Calendar

class RegisterUserInfoFragment : BaseFragment<FragmentRegisterUserInfoBinding>(
    FragmentRegisterUserInfoBinding::bind, R.layout.fragment_register_user_info
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initEvent()
        initValidation()
        validateAllInputs()
    }

    fun initData() {
        val changeText = CommonUtils.changeMultipleTextColors(
            requireContext(), "출생일과 키, 몸무게, 체육관을 \n입력해주세요",
            listOf(
                "출생일" to R.color.highlight_green,
                "키" to R.color.highlight_green,
                "몸무게" to R.color.highlight_green,
                "체육관" to R.color.highlight_green
            )
        )

        binding.apply {
            titleText.text = changeText
        }
    }

    fun initEvent() {
        binding.apply {
            btnNext.setOnClickListener {
                if (validateAllInputs()) {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                    requireActivity().finishAffinity()
                }
            }

            cvGym.setOnClickListener {
                findNavController().navigate(R.id.action_registerUserInfoFragment_to_searchGymFragment)
            }

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            // 카드 뷰 선택 시 포커스 및 키보드 올리기
            cvBirthyear.setOnClickListener {
                etBirth.requestFocus()
                imm.showSoftInput(etBirth, InputMethodManager.SHOW_IMPLICIT)
            }

            cvHeight.setOnClickListener {
                etHeight.requestFocus()
                imm.showSoftInput(etHeight, InputMethodManager.SHOW_IMPLICIT)
            }

            cvWeight.setOnClickListener {
                etWeight.requestFocus()
                imm.showSoftInput(etWeight, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    private fun initValidation() {
        binding.etHeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateHeight()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etBirth.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateBirth()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateWeight()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun validateAllInputs(): Boolean {
        val isHeightValid = validateHeight()
        val isBirthValid = validateBirth()
        val isWeightValid = validateWeight()
        return isHeightValid && isBirthValid && isWeightValid
    }

    private fun validateHeight(): Boolean {
        val heightStr = binding.etHeight.text.toString()
        val height = heightStr.toIntOrNull()
        return if (height != null && height !in 100..250) {
            binding.tvHeightError.text = "키는 100~250cm 사이여야 합니다"
            binding.tvHeightError.visibility = View.VISIBLE
            false
        } else {
            binding.tvHeightError.visibility = View.GONE
            true
        }
    }

    private fun validateWeight(): Boolean {
        val weightStr = binding.etWeight.text.toString()
        val weight = weightStr.toIntOrNull()
        return if (weight != null && weight !in 30..200) {
            binding.tvWeightError.text = "몸무게는 30~400kg 사이여야 합니다"
            binding.tvWeightError.visibility = View.VISIBLE
            false
        } else {
            binding.tvWeightError.visibility = View.GONE
            true
        }
    }

    private fun validateBirth(): Boolean {
        val birthStr = binding.etBirth.text.toString()
        if (birthStr.isEmpty()) {
            binding.tvBirthError.visibility = View.GONE
            return false
        }

        if (!birthStr.matches(Regex("^\\d{8}\$"))) {
            binding.tvBirthError.text = "생년월일은 8자리 숫자(yyyyMMdd)여야 합니다"
            binding.tvBirthError.visibility = View.VISIBLE
            return false
        }

        try {
            val year = birthStr.substring(0, 4).toInt()
            val month = birthStr.substring(4, 6).toInt()
            val day = birthStr.substring(6, 8).toInt()

            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            if (year !in 1900..currentYear) throw Exception()
            if (month !in 1..12) throw Exception()

            val daysInMonth = arrayOf(
                31, if (isLeapYear(year)) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
            )
            if (day !in 1..daysInMonth[month - 1]) throw Exception()

            binding.tvBirthError.visibility = View.GONE
            return true
        } catch (e: Exception) {
            binding.tvBirthError.text = "올바른 생년월일을 입력해주세요"
            binding.tvBirthError.visibility = View.VISIBLE
            return false
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

}