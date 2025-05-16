package com.ssafy.presentation.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ssafy.domain.model.home.PtScheduleItem
import com.ssafy.domain.model.home.ScheduleInfo
import com.ssafy.locket.utils.CalendarUtils.displayText
import com.ssafy.presentation.R
import com.ssafy.presentation.common.MainActivity
import com.ssafy.presentation.databinding.FragmentPtCalendarBottomSheetBinding
import com.ssafy.presentation.home.adapter.PtScheduleAdapter
import com.ssafy.presentation.home.viewModel.ScheduleInfoState
import com.ssafy.presentation.home.viewModel.ScheduleViewModel
import com.ssafy.presentation.home.viewModel.SelectedDayState
import com.ssafy.presentation.home.viewModel.SelectedDayViewModel
import dagger.hilt.android.internal.managers.ViewComponentManager
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "PtCalendarBottomSheetFr"
class PtCalendarBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding : FragmentPtCalendarBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private val selectedDayViewModel: SelectedDayViewModel by activityViewModels()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPtCalendarBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEvent()
        setupInitView()
    }

    fun initView(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedDayViewModel.selectedDay.collect { uiState ->
                    when(uiState) {
                        is SelectedDayState.Exist -> {
                            val day = uiState.day
                            binding.tvDate.text = String.format(
                                getString(R.string.home_bottom_sheet_date),
                                day.dayOfMonth.toString(),
                                day.dayOfWeek.displayText()
                            )
                        }
                        else -> dismissSmoothly()
                    }
                }
            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(mContext!!, R.style.CustomDialog)
        dialog.window?.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setDimAmount(0.7f)
        }
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setupRatio(bottomSheetDialog)
            dialog.setCanceledOnTouchOutside(true)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet.layoutParams

        // MATCH_PARENT 대신 적당한 높이를 설정
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams

        behavior.peekHeight = getBottomSheetDialogDefaultHeight()
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return (getWindowHeight() * 0.6).toInt() // 전체 높이의 60%로 설정
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        (getActivityContext(requireContext()) as MainActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    override fun onStop() {
        super.onStop()
        selectedDayViewModel.clearSelectedDay()
        dismissSmoothly()
    }

    fun getActivityContext(context: Context): Context {
        return if (context is ViewComponentManager.FragmentContextWrapper) {
            context.baseContext
        } else {
            context
        }
    }

    fun dismissSmoothly() {
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 필요한 경우 추가 효과
            }
        })

        // 상태를 HIDDEN으로 설정하여 자연스럽게 사라지도록 함
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun initEvent(){
        binding.btnClose.setOnClickListener {
            dismissSmoothly()
        }
    }

    fun setupInitView(){
        val schedules = arguments?.getParcelableArrayList<ScheduleInfo>("arg_schedules")

        val formatter = DateTimeFormatter.ofPattern("a h:mm") // 오전/오후 10:00 형식

        val scheduleItems = schedules!!.map { schedule ->
            val start = LocalDateTime.parse(schedule.startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME).plusHours(9)
            val end = LocalDateTime.parse(schedule.endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME).plusHours(9)
            val timeRange = "${start.format(formatter)} ~ ${end.format(formatter)}"
            PtScheduleItem(schedule.trainerName, timeRange)
        }

        val adapter = PtScheduleAdapter(scheduleItems) { item ->
            // 클릭 리스너 (필요 시 작성)
        }

        binding.tvCount.text = "PT 총 "+schedules.size.toString()+"건"
        binding.rvPayment.adapter = adapter
        binding.rvPayment.layoutManager = LinearLayoutManager(requireContext())
    }

    companion object {
        private const val ARG_SCHEDULES = "arg_schedules"
        fun newInstance(schedules: ArrayList<ScheduleInfo>): PtCalendarBottomSheetFragment {
            return PtCalendarBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_SCHEDULES, ArrayList(schedules)) // ✅ 변환
                }
            }
        }
    }
}