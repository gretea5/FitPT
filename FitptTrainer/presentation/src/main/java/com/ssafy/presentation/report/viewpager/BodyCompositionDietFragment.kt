package com.ssafy.presentation.report.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.onesoftdigm.fitrus.device.sdk.FitrusDevice
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentBodyCompositionDietBinding
import com.ssafy.presentation.report.viewmodel.ReportViewModel

class BodyCompositionDietFragment : BaseFragment<FragmentBodyCompositionDietBinding>(
    FragmentBodyCompositionDietBinding::bind,
    R.layout.fragment_body_composition_diet
) {
    private val viewModel: ReportViewModel by activityViewModels()
    private lateinit var manager: FitrusDevice
    private var measuring: Boolean = false
    private var type: String = "comp"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvevt()
    }

    fun initEvevt() {
        binding.apply {
            etReportDietContent.addTextChangedListener {
                viewModel.setReportComment(it.toString())
            }
        }
    }
}