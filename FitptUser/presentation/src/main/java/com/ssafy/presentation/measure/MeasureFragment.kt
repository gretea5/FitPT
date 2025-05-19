package com.ssafy.presentation.measure

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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.onesoftdigm.fitrus.device.sdk.FitrusBleDelegate
import com.onesoftdigm.fitrus.device.sdk.FitrusDevice
import com.onesoftdigm.fitrus.device.sdk.Gender
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.domain.model.login.UserInfo
import com.ssafy.domain.model.measure.BodyInfoItem
import com.ssafy.domain.model.measure.CompositionDetail
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentLoginBinding
import com.ssafy.presentation.databinding.FragmentMeasureBinding
import com.ssafy.presentation.home.viewModel.UserInfoViewModel
import com.ssafy.presentation.measurement_record.adapter.BodyInfoAdapter
import com.ssafy.presentation.measurement_record.viewModel.CreateBodyInfoState
import com.ssafy.presentation.measurement_record.viewModel.GetBodyDetailInfoState
import com.ssafy.presentation.measurement_record.viewModel.MeasureViewModel
import com.ssafy.presentation.util.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round


private const val TAG = "MeasureFragment"

@AndroidEntryPoint
class MeasureFragment : BaseFragment<FragmentMeasureBinding>(
    FragmentMeasureBinding::bind,
    R.layout.fragment_measure
), FitrusBleDelegate {
    private lateinit var manager: FitrusDevice
    private var measuring: Boolean = false
    private var type: String = "comp"
    private lateinit var dialog: ProgressDialog

    @Inject
    lateinit var userDataStoreSource: UserDataStoreSource

    private val measureViewModel: MeasureViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = ProgressDialog(requireContext())
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        observeBodyDetailList()
        initial()
        initEvent()
    }


    fun initial(){
        binding.rvBodyCompositionResult.isVisible = false
        manager = FitrusDevice(requireActivity(), this, "normal_key")
    }

    fun initEvent(){
        binding.btnBleStart.setOnClickListener {
            bluetoothConnect()
        }

        binding.btnWeight.setOnClickListener {
            binding.ivFitrusBle.isVisible = true
            binding.tvBleDescription.isVisible = true
            binding.btnBleStart.isVisible = true
            binding.btnWeight.isVisible = false
            binding.tvWeight.isVisible = false
            binding.etWeight.isVisible = false
        }
        binding.etWeight.addTextChangedListener(object : TextWatcher {
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
            Log.d(TAG,"클릭하였습니다.")
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
                    val user = userDataStoreSource.user.first()!!
                    Log.d(TAG,user.memberBirth.toString())
                    manager.startFitrusCompMeasure(
                        Gender.valueOf(if (user.memberGender == "남성") "MALE" else "FEMALE"),
                        user.memberHeight.toFloat(),
                        binding.etWeight.text.toString().toFloat(),
                        CommonUtils.formatMeasureCreatedAt(user.memberBirth),
                        0.0f,
                    )
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
            val user = userDataStoreSource.user.first()!!
            val detail = CompositionDetail(
                bfm = result["bfm"]?.toDoubleOrNull() ?: 0.0,
                bfp = result["bfp"]?.toDoubleOrNull() ?: 0.0,
                bmr = result["bmr"]?.toDoubleOrNull() ?: 0.0,
                bodyAge = result["bodyAge"]?.toIntOrNull() ?: 0,
                ecw = result["ecw"]?.toDoubleOrNull() ?: 0.0,
                icw = result["icw"]?.toDoubleOrNull() ?: 0.0,
                memberId = user.memberId.toLong(),
                mineral = result["mineral"]?.toDoubleOrNull() ?: 0.0,
                protein = result["protein"]?.toDoubleOrNull() ?: 0.0,
                smm = result["smm"]?.toDoubleOrNull() ?: 0.0,
                weight = binding.etWeight.text.toString().toDouble()
            )
            measureViewModel.createBody(detail) {
                measuring = false
                showToast("측정이 완료되어 개인 측정에 추가되었습니다")
                manager.disconnectFitrus()
            }
        }
    }

    override fun handleFitrusConnected() {
        binding.btnMeasureStart.isVisible = true
        binding.btnMeasureStart.isEnabled = true
        binding.btnBleStart.isVisible = false
        binding.btnBleStart.isEnabled = false
        binding.ivFitrusBle.isVisible = false
        binding.tvBleDescription.isVisible = false
        binding.ivFitrusUse.isVisible = true
        binding.tvDescription.isVisible = true
    }

    override fun handleFitrusDeviceInfo(result: Map<String, String>) {
        TODO("Not yet implemented")
    }

    override fun handleFitrusDisconnected() {
        binding.btnMeasureStart.isVisible = false
        binding.btnMeasureStart.isEnabled = false
        binding.ivFitrusUse.isVisible = false
        binding.tvDescription.isVisible = false
        binding.rvBodyCompositionResult.isVisible = true
        val state = measureViewModel.createBodyInfo.value
        if(state is CreateBodyInfoState.Success){
            measureViewModel.getBodyDetailInfo(state.compositionLog)
        }
        binding.etWeight.text.clear()
    }

    override fun handleFitrusPpgMeasured(result: Map<String, Any>) {
        TODO("Not yet implemented")
    }

    override fun handleFitrusTempMeasured(result: Map<String, String>) {
        TODO("Not yet implemented")
    }

    private fun observeBodyDetailList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                measureViewModel.getBodyDetailInfo.collect { state ->
                    when (state) {
                        is GetBodyDetailInfoState.Loading -> {
                        }

                        is GetBodyDetailInfoState.Success -> {
                            val info = state.getBodydetail
                            Log.d(TAG,"시작되었습니다"+info.toString())

                            // 소수점 한 자리로 포맷
                            fun format1(value: Double): String = String.format("%.1f", value)
                            val sampleItems = listOf(
                                BodyInfoItem("체지방률", format1(info.bfp) + " %", info.bfpLabel),
                                BodyInfoItem("골격근량", format1(info.smm) + " kg", info.smmLabel),
                                BodyInfoItem("체지방량", format1(info.bfm) + " kg", info.bfmLabel),
                                BodyInfoItem("기초대사량", format1(info.bmr) + " kcal", "적정"),
                                BodyInfoItem("단백질", format1(info.protein) + " kg", info.proteinLabel),
                                BodyInfoItem("무기질", format1(info.mineral) + " kg", info.mineralLabel),
                                BodyInfoItem("세포외수분비", format1(info.ecw) + " kg", info.ecwRatioLabel),
                            )

                            val adapter = BodyInfoAdapter(sampleItems) {

                            }
                            binding.rvBodyCompositionResult.adapter = adapter
                            binding.rvBodyCompositionResult.layoutManager =
                                LinearLayoutManager(requireContext())
                        }

                        is GetBodyDetailInfoState.Error -> {

                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}