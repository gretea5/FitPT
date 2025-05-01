package com.ssafy.presentation.mypage

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.ssafy.presentation.databinding.FragmentLogoutDialogBinding

class LogoutDialogFragment : DialogFragment() {
    private var _binding: FragmentLogoutDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        _binding = FragmentLogoutDialogBinding.inflate(layoutInflater)
        val view = binding.root
        dialog.setContentView(view)

        binding.btnLogout.setOnClickListener {
            //performLogout()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}