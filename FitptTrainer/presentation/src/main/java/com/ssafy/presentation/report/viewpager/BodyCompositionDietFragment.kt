package com.ssafy.presentation.report.viewpager

import android.app.ProgressDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.onesoftdigm.fitrus.device.sdk.FitrusBleDelegate
import com.onesoftdigm.fitrus.device.sdk.FitrusDevice
import com.onesoftdigm.fitrus.device.sdk.Gender
import com.ssafy.domain.model.measure.CompositionCondition
import com.ssafy.domain.model.measure.CompositionDetail
import com.ssafy.domain.model.measure.CompositionDetailItem
import com.ssafy.domain.model.measure.CompositionItem
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentBodyCompositionDietBinding
import com.ssafy.presentation.home.viewmodel.HomeViewModel
import com.ssafy.presentation.report.ReportEditFragmentArgs
import com.ssafy.presentation.report.adapter.CompositionAdapter
import com.ssafy.presentation.report.viewmodel.CreateBodyInfoState
import com.ssafy.presentation.report.viewmodel.GetBodyDetailInfoState
import com.ssafy.presentation.report.viewmodel.GetReportInfoState
import com.ssafy.presentation.report.viewmodel.MeasureViewModel
import com.ssafy.presentation.report.viewmodel.ReportViewModel
import com.ssafy.presentation.report.viewmodel.UserInfoState
import com.ssafy.presentation.util.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "BodyCompositionDietFrag"
@AndroidEntryPoint
class BodyCompositionDietFragment : BaseFragment<FragmentBodyCompositionDietBinding>(
    FragmentBodyCompositionDietBinding::bind,
    R.layout.fragment_body_composition_diet
), FitrusBleDelegate {
    private val viewModel: ReportViewModel by activityViewModels()


    private val measureViewModel : MeasureViewModel by activityViewModels()
    private val reportViewModel : ReportViewModel by activityViewModels()

    private lateinit var manager: FitrusDevice
    private var measuring: Boolean = false
    private var type: String = "comp"
    private lateinit var dialog: ProgressDialog
    private var memberId : Long? = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = ProgressDialog(requireContext())
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)

        initEvent()
        if(reportViewModel.reportId.value==-1){
            initial()
        }
        else{
            observeBodyList()
            observeReportDetailList()
        }
    }

    fun initial(){
        manager = FitrusDevice(requireActivity(), this, "normal_key")
        memberId = measureViewModel.memberId.value.toLong()
        observeBodyList()
    }

    fun initEvent() {
        binding.apply {
            etReportDietContent.addTextChangedListener {
                viewModel.setReportComment(it.toString())
            }
        }
        binding.btnBleStart.setOnClickListener {
            bluetoothConnect()
        }

        binding.btnWeight.setOnClickListener {
            binding.clWeight.isVisible = false
            binding.clReportBleExplanation.isVisible = true
        }
        binding.etWeightInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val weightStr = s.toString()
                val weight = weightStr.toFloatOrNull()
                if (weight != null && weight >= 30 && weight < 200) {
                    binding.btnWeight.isEnabled = true
                    binding.btnWeight.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.main_orange))
                } else {
                    binding.btnWeight.isEnabled = false
                    binding.btnWeight.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.disabled))
                }
            }
        })

        binding.btnMeasureStart.setOnClickListener {
            if (measuring) return@setOnClickListener
            measuring = true

            if (type in listOf("device", "battery", "tempObj"))
                measureStart()
            else {
                if (manager.fitrusConnectionState) {
                    object : CountDownTimer(5000, 1000) {
                        override fun onTick(p0: Long) {
                            dialog.show()
                        }
                        override fun onFinish() {
                            measureStart()
                        }
                    }.start()
                } else {
                }
            }
        }

    }
    fun bluetoothConnect(){
        if (manager.fitrusConnectionState) {

        } else {
            if (manager.fitrusScanState) {
                manager.startFitrusScan()
            } else {
                showToast("블루투스 사용 불가")
            }
        }
    }

    fun measureStart() {
        dialog.show()
        when (type) {
            "comp" -> {
                lifecycleScope.launch {
                    val state = measureViewModel.userInfo.value
                    if (state is UserInfoState.Success) {
                        val user = state.userInfo
                        manager.startFitrusCompMeasure(
                            Gender.valueOf(if (user.memberGender == "남성") "MALE" else "FEMALE"),
                            user.memberHeight.toFloat(),
                            binding.etWeightInput.text.toString().toFloat(),
                            CommonUtils.formatMeasureCreatedAt(user.memberBirth),
                            0.0f,
                        )
                    }
                }
            }
        }
    }

    override fun fitrusDispatchError(error: String) {
        if (dialog.isShowing) dialog.dismiss()
        showToast("제대로 측정해주세요")
    }

    override fun handleFitrusBatteryInfo(result: Map<String, Any>) {
        TODO("Not yet implemented")
    }

    override fun handleFitrusCompMeasured(result: Map<String, String>) {
        if (dialog.isShowing) dialog.dismiss()
        lifecycleScope.launch {
            val detail = CompositionDetail(
                bfm = result["bfm"]?.toDoubleOrNull() ?: 0.0,
                bfp = result["bfp"]?.toDoubleOrNull() ?: 0.0,
                bmr = result["bmr"]?.toDoubleOrNull() ?: 0.0,
                bodyAge = result["bodyAge"]?.toIntOrNull() ?: 0,
                ecw = result["ecw"]?.toDoubleOrNull() ?: 0.0,
                icw = result["icw"]?.toDoubleOrNull() ?: 0.0,
                memberId = memberId!!.toLong(),
                mineral = result["mineral"]?.toDoubleOrNull() ?: 0.0,
                protein = result["protein"]?.toDoubleOrNull() ?: 0.0,
                smm = result["smm"]?.toDoubleOrNull() ?: 0.0,
                weight = binding.etWeightInput.text.toString().toDouble()
            )
            measureViewModel.createBody(detail) {
                measuring = false
                showToast("측정이 완료되어 개인 측정에 추가되었습니다")
                manager.disconnectFitrus()
            }
        }
    }

    override fun handleFitrusConnected() {
        binding.clReportBleExplanation.isVisible = false
        binding.clReportMeasureExplanation.isVisible = true
    }

    override fun handleFitrusDeviceInfo(result: Map<String, String>) {
        TODO("Not yet implemented")
    }

    override fun handleFitrusDisconnected() {
        binding.clReportMeasureExplanation.isVisible = false
        binding.scrollviewMeasureResult.isVisible = true
        val state = measureViewModel.measureCreateInfo.value
        if(state is CreateBodyInfoState.Success){
            val compositionLog = state.measureCreate
            measureViewModel.getBodyDetailInfo(compositionLog)
            Log.d(TAG,"끝났습니다")
        }
        else if(state is CreateBodyInfoState.Error){
            Log.d(TAG,"실패하였습니다.")
        }
    }

    override fun handleFitrusPpgMeasured(result: Map<String, Any>) {
        TODO("Not yet implemented")
    }

    override fun handleFitrusTempMeasured(result: Map<String, String>) {
        TODO("Not yet implemented")
    }

    private fun observeBodyList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                measureViewModel.getBodyDetailInfo.collect { state ->
                    when (state) {
                        is GetBodyDetailInfoState.Loading -> {
                        }
                        is GetBodyDetailInfoState.Success -> {
                            Log.d(TAG,"측정 상세 값"+state.getBodydetail.toString())
                            val bodyDetail = state.getBodydetail
                            Log.d(TAG, "측정 상세 값: $bodyDetail")

                            val list = convertToConditionList(bodyDetail)
                            val adapter = CompositionAdapter(list)
                            binding.rvReportMeasureResult.adapter = adapter
                            binding.rvReportMeasureResult.layoutManager = LinearLayoutManager(requireContext())
                        }
                        is GetBodyDetailInfoState.Error -> {

                        }
                        else -> Unit
                    }
                }
            }
        }


    }

    private fun convertToConditionList(data: CompositionDetailItem): List<CompositionCondition> {
        return listOf(
            //CompositionCondition("몸무게", data.weightLabel, data.weightCount,data.weight.toString()),
            CompositionCondition("체지방량", data.bfmLabel, data.bfmCount,data.bfm.toString()),
            CompositionCondition("체지방률", data.bfpLabel, data.bfpCount,data.bfp.toString()),
            CompositionCondition("골격근량", data.smmLabel, data.smmCount,data.smm.toString()),
            //CompositionCondition("BMI", data.bmiLabel, data.bmiCount,),
            CompositionCondition("체수분량", data.tcwLabel, data.tcwCount,data.icw.toString()),
            CompositionCondition("단백질", data.proteinLabel, data.proteinCount,data.protein.toString()),
            CompositionCondition("무기질", data.mineralLabel, data.mineralCount,data.mineral.toString()),
            CompositionCondition("세포외수분비", data.ecwRatioLabel, data.ecwRatioCount,data.ecw.toString())
        )
    }


    private fun observeReportDetailList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getReportDetailInfo.collect { state ->
                    when (state) {
                        is GetReportInfoState.Loading -> {
                        }

                        is GetReportInfoState.Success -> {
                            val reportDetail = state.getReportdetail
                            binding.etReportDietContent.setText(reportDetail.reportComment)
                            measureViewModel.getBodyDetailInfo(reportDetail.compositionResponseDto.compositionLogId)
                            binding.clWeight.isVisible = false
                            binding.scrollviewMeasureResult.isVisible = true
                            Log.d(TAG, reportDetail.toString())
                        }

                        is GetReportInfoState.Error -> {

                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}