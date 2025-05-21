package com.ssafy.presentation.schedule

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import java.time.YearMonth
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexboxLayout
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.ssafy.domain.model.member.MemberInfo
import com.ssafy.domain.model.schedule.Schedule
import com.ssafy.presentation.databinding.FragmentScheduleBinding
import com.ssafy.presentation.schedule.adapter.ScheduleMemberAdapter
import com.ssafy.presentation.schedule.viewmodel.ScheduleViewModel
import com.ssafy.presentation.util.TimeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private const val TAG = "ScheduleFrasgment_ssafy"

@AndroidEntryPoint
class ScheduleFragment : BaseFragment<FragmentScheduleBinding>(
    FragmentScheduleBinding::bind,
    R.layout.fragment_schedule
) {
    private val timeButtonsMap = mutableMapOf<String, Button>()
    private val eventsDatesList = mutableListOf<LocalDate>()
    private val viewModel : ScheduleViewModel by viewModels()

    private val morningTimeList = listOf(
        "09:00",
        "10:00",
        "11:00",
    )

    private val afternoonTimeList = listOf(
        "12:00",
        "13:00",
        "14:00",
        "15:00",
        "16:00",
        "17:00",
        "18:00",
        "19:00",
        "20:00",
    )

    private var selectedButton: Button? = null
    private var selectedDate: LocalDate? = null

    private val clickListener = View.OnClickListener { view ->
        val button = view as Button

        selectedButton?.let { it.isSelected = false }

        if (selectedButton == button) {
            button.isSelected = false
            selectedButton = null
        } else {
            button.isSelected = true
            selectedButton = button
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initCalendar()
        initObserver()
        initButtonView()
        initEvent()
        fetchSchedules()
        fetchMembers()
    }

    private fun initAdapter() {
        val adapter = ScheduleMemberAdapter(requireContext(), emptyList())
        binding.sMember.adapter = adapter
    }

    private fun initCalendar() {
        val currentMonth = YearMonth.now()

        val startMonth = currentMonth.minusMonths(120)
        val endMonth = currentMonth.plusMonths(120)
        val firstDayOfWeek = firstDayOfWeekFromLocale()

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val titlesContainer = view as ViewGroup
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            val textView = view.findViewById<TextView>(R.id.item_calendar_day_tv)
            var day: CalendarDay? = null

            init {
                view.setOnClickListener {
                    day?.let { day ->
                        if (day.position == DayPosition.MonthDate) {
                            if (selectedDate != day.date) {
                                val oldDate = selectedDate
                                selectedDate = day.date

                                binding.calendar.notifyDateChanged(day.date)
                                oldDate?.let { binding.calendar.notifyDateChanged(it) }

                                selectSchedulesByDate(selectedDate!!)
                            }
                        }
                    }
                }
            }
        }

        binding.calendar.apply {
            monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)

                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    if (container.titlesContainer.tag == null) {
                        container.titlesContainer.tag = data.yearMonth
                        val daysOfWeek = daysOfWeek()

                        container.titlesContainer.children.map { it as TextView }
                            .forEachIndexed { index, textView ->
                                val dayOfWeek = daysOfWeek[index]
                                val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                                textView.text = title

                                when (dayOfWeek) {
                                    DayOfWeek.SUNDAY -> textView.setTextColor(Color.RED)
                                    DayOfWeek.SATURDAY -> textView.setTextColor(Color.BLUE)
                                    else -> textView.setTextColor(Color.BLACK)
                                }
                            }
                    }
                }
            }

            dayBinder = object : MonthDayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)

                override fun bind(container: DayViewContainer, data: CalendarDay) {
                    container.day = data
                    container.textView.text = data.date.dayOfMonth.toString()

                    if (data.position == DayPosition.MonthDate) {
                        when (data.date.dayOfWeek) {
                            DayOfWeek.SUNDAY -> container.textView.setTextColor(Color.RED)
                            DayOfWeek.SATURDAY -> container.textView.setTextColor(Color.BLUE)
                            else -> container.textView.setTextColor(Color.BLACK)
                        }

                        if (data.date == selectedDate) {
                            container.textView.setBackgroundResource(R.drawable.bg_selected_day)
                        }
                        else if (eventsDatesList.contains(data.date)) {
                            container.textView.setBackgroundResource(R.drawable.bg_event_day)
                        } else {
                            container.textView.background = null
                        }
                    } else {
                        container.textView.setTextColor(Color.GRAY)
                        container.textView.background = null
                    }
                }
            }

            setup(startMonth, endMonth, firstDayOfWeek)
            scrollToMonth(currentMonth)

            monthScrollListener = { calendarMonth ->
                val year = calendarMonth.yearMonth.year
                val month = calendarMonth.yearMonth.monthValue.toString().padStart(2, '0')
                val formattedDate = "$year-$month"

                binding.tvMonthYear.text = formattedDate

                selectedButton?.apply { isSelected = false }

                selectedButton = null

                viewModel.getMonthlySchedules(formattedDate)

                timeButtonsMap.values.forEach { button ->
                    button.apply {
                        setBackgroundResource(R.drawable.selector_button_time)
                        typeface = ResourcesCompat.getFont(context, R.font.pretendard_medium)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                        setTextColor(ContextCompat.getColor(context, R.color.main_black))
                        includeFontPadding = false
                        setOnClickListener(clickListener)
                        isEnabled = true

                        val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
                        val margin8dp = (8 * resources.displayMetrics.density).toInt()
                        layoutParams.topMargin = margin8dp
                        layoutParams.bottomMargin = margin8dp
                        this.layoutParams = layoutParams
                    }
                }
            }
        }
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.schedules.collect { schedules ->
                Log.d(TAG, "initObserver: $schedules")
                updateScheduleButtonColors(schedules, timeButtonsMap)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.createdScheduleId.collect { scheduleId ->
                scheduleId?.let {
                    findNavController().popBackStack()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.monthlyScheduleItems.collect { scheduleItems ->
                    updateEventsDatesList(scheduleItems)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.members.collect() { members -> updateMemberAdapter(members) }
            }
        }
    }

    private fun updateScheduleButtonColors(
        schedules: List<Schedule>,
        timeButtons: Map<String, Button>
    ) {
        timeButtons.values.forEach { button ->
            button.apply {
                setBackgroundResource(R.drawable.selector_button_time)
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_medium)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                setTextColor(ContextCompat.getColor(context, R.color.main_black))
                includeFontPadding = false
                setOnClickListener(clickListener)
                isEnabled = true

                val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
                val margin8dp = (8 * resources.displayMetrics.density).toInt()
                layoutParams.topMargin = margin8dp
                layoutParams.bottomMargin = margin8dp
                this.layoutParams = layoutParams
            }
        }

        schedules.forEach { schedule ->
            val timeKey = TimeUtils.parseDateTime(schedule.startTime).second

            timeButtons[timeKey]?.let { button ->
                button.apply {
                    setBackgroundResource(R.drawable.bg_stroke_gray_8dp)
                    typeface = ResourcesCompat.getFont(context, R.font.pretendard_medium)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                    setTextColor(ContextCompat.getColor(button.context, R.color.main_gray))
                    includeFontPadding = false
                    isEnabled = false

                    val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
                    val margin8dp = (8 * resources.displayMetrics.density).toInt()
                    layoutParams.topMargin = margin8dp
                    layoutParams.bottomMargin = margin8dp
                    this.layoutParams = layoutParams
                }
            }
        }
    }

    private fun updateMemberAdapter(members: List<MemberInfo>) {
        val adapter = ScheduleMemberAdapter(requireContext(), members)
        binding.sMember.adapter = adapter
    }

    private fun initButtonView() {
        morningTimeList.forEach { time ->
            val button = Button(requireContext()).apply {
                layoutParams = FlexboxLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    flexBasisPercent = 0.3f  // 30%에 해당
                    setMargins(4, 4, 4, 4)
                }
                text = time
                setBackgroundResource(R.drawable.selector_button_time)
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_medium)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                setTextColor(ContextCompat.getColor(context, R.color.main_black))
                includeFontPadding = false
                setOnClickListener(clickListener)

                val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
                val margin8dp = (8 * resources.displayMetrics.density).toInt()
                layoutParams.topMargin = margin8dp
                layoutParams.bottomMargin = margin8dp
                this.layoutParams = layoutParams
            }

            timeButtonsMap[time] = button
            binding.fbMidButton.addView(button)
        }

        afternoonTimeList.forEach { time ->
            val button = Button(requireContext()).apply {
                layoutParams = FlexboxLayout.LayoutParams(0,  ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    flexBasisPercent = 0.3f  // 30%에 해당
                    setMargins(4, 4, 4, 4)
                }
                text = time
                setBackgroundResource(R.drawable.selector_button_time)
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_medium)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                setTextColor(ContextCompat.getColor(context, R.color.main_black))
                includeFontPadding = false
                setOnClickListener(clickListener)

                val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
                val margin8dp = (8 * resources.displayMetrics.density).toInt()
                layoutParams.topMargin = margin8dp
                layoutParams.bottomMargin = margin8dp
                this.layoutParams = layoutParams
            }

            timeButtonsMap[time] = button
            binding.fbAfternoonButton.addView(button)
        }
    }

    private fun initEvent() {
        binding.sMember.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMember = parent?.getItemAtPosition(position) as MemberInfo
                binding.tvMemberName.text = selectedMember.memberName
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.tvMemberName.text = "회원 선택"
            }
        }

        binding.tvMemberName.setOnClickListener {
            binding.sMember.performClick()
        }

        binding.btnRegister.setOnClickListener {
            createSchedule()
        }
    }

    private fun fetchSchedules() {
        val today = LocalDate.now()
        selectedDate = today

        val formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val formattedMonth = today.format(DateTimeFormatter.ofPattern("yyyy-MM"))

        viewModel.getSchedules(
            month = null,
            date = formattedDate,
            trainerId = null,
            memberId = null
        )

        viewModel.getMonthlySchedules(formattedMonth)
    }

    private fun fetchMembers() {
        viewModel.getMembers()
    }

    private fun selectSchedulesByDate(date: LocalDate) {
        selectedButton?.apply { isSelected = false }

        selectedButton = null

        selectedDate = date
        val formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        viewModel.getSchedules(
            date = formattedDate,
            month = null,
            trainerId = null,
            memberId = null
        )
    }
    
    private fun updateEventsDatesList(schedules: List<Schedule>) {
        eventsDatesList.clear()

        val eventDates = schedules.mapNotNull { schedule ->
            try {
                val dateStr = TimeUtils.parseDateTime(schedule.startTime).first
                LocalDate.parse(dateStr)
            } catch (e: Exception) {
                Log.e(TAG, "날짜 파싱 오류: ${schedule.startTime}, ${e.message}")
                null
            }
        }.distinct()

        eventsDatesList.addAll(eventDates)

        binding.calendar.notifyCalendarChanged()
    }

    private fun createSchedule() {
        val selectedMember = binding.sMember.selectedItem as MemberInfo
        val memberId = selectedMember.memberId

        val selectedDate = this.selectedDate
        if (selectedDate == null) {
            showToast("날짜를 선택해주세요")
            return
        }

        val selectedButton = this.selectedButton
        if (selectedButton == null) {
            showToast("시간을 선택해주세요")
            return
        }

        val scheduleContent = binding.etDetail.text.toString()
        if (scheduleContent.isBlank()) {
            showToast("일정 내용을 입력해주세요")
            return
        }

        val (startTime, endTime) = TimeUtils.calculateScheduleTimes(selectedDate, selectedButton.text.toString())

        Log.d(TAG, "createSchedule: $memberId $startTime $endTime $scheduleContent")

        viewModel.createSchedule(memberId, startTime, endTime, scheduleContent)
    }
}
