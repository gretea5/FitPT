package com.ssafy.presentation.schedule

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children
import com.google.android.flexbox.FlexboxLayout
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentScheduleEditBinding
import com.ssafy.presentation.schedule.adapter.Member
import com.ssafy.presentation.schedule.adapter.ScheduleMemberAdapter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


class ScheduleEditFragment : BaseFragment<FragmentScheduleEditBinding>(
    FragmentScheduleEditBinding::bind,
    R.layout.fragment_schedule_edit
) {
    private val eventsDatesList = mutableListOf<LocalDate>()

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
        "21:00",
        "22:00",
    )

    private val memberList = listOf(
        Member("박장훈", "2000.01.07"),
        Member("안세호", "1997.07.11"),
        Member("김두영", "1998.05.29"),
        Member("김기훈", "1997.09.27"),
        Member("김동현", "1999.07.30"),
        Member("관경탁", "1996.12.31")
    )

    private val selectedButtons = mutableListOf<Button>()

    private var selectedDate: LocalDate? = null

    private val clickListener = View.OnClickListener { view ->
        val button = view as Button

        if (button.isSelected) {
            button.isSelected = false
            selectedButtons.remove(button)
        } else {
            button.isSelected = true
            selectedButtons.add(button)
        }

        binding.btnRegister.isEnabled = selectedButtons.isNotEmpty()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initCalendar()
        initButtonView()
        initEvent()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addExampleEventList() {
        val today = LocalDate.now()

        eventsDatesList.add(today.plusDays(3))
        eventsDatesList.add(today.plusDays(7))
        eventsDatesList.add(today.plusDays(12))
        eventsDatesList.add(today.minusDays(2))
    }

    private fun initAdapter() {
        val adapter = ScheduleMemberAdapter(requireContext(), memberList)
        binding.sMember.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initCalendar() {
        val currentMonth = YearMonth.now()

        addExampleEventList()

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

            // dayBinder 수정
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
                val monthYear = "${calendarMonth.yearMonth.year}/${calendarMonth.yearMonth.monthValue}월"
                binding.tvMonthYear.text = monthYear
            }
        }
    }

    fun setEventsData(events: List<LocalDate>) {
        eventsDatesList.clear()
        eventsDatesList.addAll(events)
        binding.calendar.notifyCalendarChanged()
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
                setTextColor(Color.BLACK)
                setOnClickListener(clickListener)
            }

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
                setTextColor(Color.BLACK)
                setOnClickListener(clickListener)
            }

            binding.fbAfternoonButton.addView(button)
        }
    }

    private fun initEvent() {
        binding.sMember.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMember = parent?.getItemAtPosition(position) as Member
                binding.tvMemberName.text = selectedMember.name
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                binding.tvMemberName.text = "회원 선택"
            }
        }

        binding.tvMemberName.setOnClickListener {
            binding.sMember.performClick()
        }
    }
}