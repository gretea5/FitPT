package com.ssafy.presentation.home

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentHomeBinding
import java.time.LocalDate
import java.time.YearMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.ssafy.locket.utils.CalendarUtils.displayText
import com.ssafy.presentation.databinding.CalendarDayBinding
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter

class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind,
    R.layout.fragment_home
) {
    private var selectedDate = LocalDate.now()
    private val today = LocalDate.now()
    private val startMonth = YearMonth.of(2020, 1)
    private val endMonth = YearMonth.of(today.year, today.monthValue)
    private val daysOfWeek = daysOfWeek(DayOfWeek.MONDAY)
    private val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val currentMonth = YearMonth.of(2025,5)
    private lateinit var dialog: PtCalendarBottomSheetFragment


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initCalendar()
        initView()
    }

    fun initEvent(){
        binding.ivNotificationMove.setOnClickListener {
            findNavController().navigate(R.id.action_home_fragment_to_notification_fragment)
        }
    }

    private fun updateDayWeekColor() {
        for ((index, dayText) in daysOfWeek.withIndex()) {
            val dayLayout = binding.layoutDow.root.getChildAt(index)
            if (dayLayout != null) {
                val textView: TextView? = dayLayout.findViewById(R.id.dayWeekText)
                if (textView != null) {
                    textView.text = dayText.displayText()
                }
            }
        }
    }

    private fun dateClicked(date: LocalDate) {
        binding.calendar.notifyDateChanged(selectedDate) // 이전 선택값 해제
        selectedDate = date
        dialog.show(childFragmentManager, "payment")
        binding.calendar.notifyDateChanged(date) // 새로운 선택값
    }

    private fun bindDate(
        date: LocalDate,
        dayText: TextView,
        paymentText: TextView,
        isSelectable: Boolean
    ) {
        dayText.text = date.dayOfMonth.toString()
        val fonts = arrayOf(R.font.pretendard_regular, R.font.pretendard_bold)

        if (isSelectable) {
            when {
                date == selectedDate -> {
                    dayText.apply {
                        setTextColor(resources.getColor(R.color.highlight_green))
                        typeface = ResourcesCompat.getFont(context, fonts[1])
                    }
                }
                else -> {
                    dayText.apply {
                        setTextColor(resources.getColor(R.color.text))
                        typeface = ResourcesCompat.getFont(context, fonts[0])
                    }
                }
            }
        } else {
            dayText.setTextColor(resources.getColor(R.color.disabled))
            paymentText.setText(null)
        }
    }

    fun initCalendar(){
        var downX = 0f
        var downY = 0f
        dialog = PtCalendarBottomSheetFragment()

        binding.calendar.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    downY = event.y
                    // false 반환 → 여기서는 이벤트를 소비하지 않음(달력 내부 로직으로 전달됨)
                    false
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = kotlin.math.abs(event.x - downX)
                    val dy = kotlin.math.abs(event.y - downY)
                    val threshold = ViewConfiguration.get(requireContext()).scaledTouchSlop

                    // 사용자가 손가락을 임계값 이상 움직였다면 = 스크롤 시도
                    if (dx > threshold || dy > threshold) {
                        // true 반환 → 이벤트 소비(스와이프/스크롤 동작 막기)
                        true
                    } else {
                        // 아직 크게 움직이지 않았으므로, 클릭(탭) 가능성 있음
                        false
                    }
                }

                // UP, CANCEL 등은 상황에 맞게 처리 (여기서는 기본적으로 false)
                else -> false
            }
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val dayBinding = CalendarDayBinding.bind(view)
            val dayText = dayBinding.day
            val paymentText = dayBinding.payment

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        dateClicked(date = day.date)
                    }
                }
            }
        }

        binding.calendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                bindDate(
                    data.date,
                    container.dayText,
                    container.paymentText,
                    data.position == DayPosition.MonthDate
                )
            }

            override fun create(view: View): DayViewContainer = DayViewContainer(view)
        }
        binding.calendar.setup(startMonth, endMonth, daysOfWeek.first())
        binding.calendar.scrollToMonth(currentMonth)
        updateDayWeekColor()

    }


    fun initView(){
        val text = "김동현님의 체성분 그래프"
        val spannableString = SpannableString(text)

        val start = text.indexOf("김동현")
        val end = start + "김동현".length
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.highlight_green)), // 원하는 색으로 변경
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvBodyGraph.text = spannableString

        val text2 = "김동현님의 PT 일정"
        val spannableString2 = SpannableString(text2)
        val start2 = text2.indexOf("김동현")
        val end2 = start2 + "김동현".length
        spannableString2.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.highlight_green)), // 원하는 색으로 변경
            start2,
            end2,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvPtCalendar.text = spannableString2
    }
}