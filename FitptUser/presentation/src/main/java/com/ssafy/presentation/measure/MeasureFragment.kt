package com.ssafy.presentation.measure

import android.app.ProgressDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.onesoftdigm.fitrus.device.sdk.FitrusBleDelegate
import com.onesoftdigm.fitrus.device.sdk.FitrusDevice
import com.onesoftdigm.fitrus.device.sdk.Gender
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentLoginBinding
import com.ssafy.presentation.databinding.FragmentMeasureBinding
import kotlin.math.round


private const val TAG = "MeasureFragment"
class MeasureFragment : BaseFragment<FragmentMeasureBinding>(
    FragmentMeasureBinding::bind,
    R.layout.fragment_measure
), FitrusBleDelegate {
    private lateinit var manager: FitrusDevice
    private var measuring: Boolean = false
    private var type: String = "comp"
    private lateinit var dialog: ProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = ProgressDialog(requireContext())
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)

        initial()
        initEvent()
    }

    fun initial(){
        manager = FitrusDevice(requireContext(), this, "normal_key")
    }

    fun initEvent(){
        binding.btnBleStart.setOnClickListener {
            bluetoothConnect()
        }

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
                manager.startFitrusCompMeasure(
                    Gender.valueOf("MALE"),
                    170f,
                    50f,
                    "1976/11/07",
                    0.0f,
                )
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
        binding.tvResult.text = result.map { "${it.key} : ${it.value}" }.joinToString("\n")
        Log.d(TAG,result.toString())
        measuring = false
        Log.d(TAG,"끊기 전입니다")
        manager.disconnectFitrus()
    }

    override fun handleFitrusConnected() {
        Log.d(TAG,"연결했습니다.")
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
        Log.d(TAG,"끊겼습니다")
        binding.btnMeasureStart.isVisible = false
        binding.btnMeasureStart.isEnabled = false
        binding.ivFitrusUse.isVisible = false
        binding.tvDescription.isVisible = false
    }

    override fun handleFitrusPpgMeasured(result: Map<String, Any>) {
        TODO("Not yet implemented")
    }

    override fun handleFitrusTempMeasured(result: Map<String, String>) {
        TODO("Not yet implemented")
    }
}