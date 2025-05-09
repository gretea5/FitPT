package com.ssafy.presentation.scheduling

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentSchedulingBinding
import java.time.YearMonth
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class SchedulingFragment : BaseFragment<FragmentSchedulingBinding>(
    FragmentSchedulingBinding::bind,
    R.layout.fragment_scheduling
) {
    private val eventsDatesList = mutableListOf<LocalDate>()

    private var selectedDate: LocalDate? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCalendar()
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
                        // 요일에 따른 색상 설정
                        when (data.date.dayOfWeek) {
                            DayOfWeek.SUNDAY -> container.textView.setTextColor(Color.RED)
                            DayOfWeek.SATURDAY -> container.textView.setTextColor(Color.BLUE)
                            else -> container.textView.setTextColor(Color.BLACK)
                        }

                        // 선택된 날짜 처리
                        if (data.date == selectedDate) {
                            container.textView.setBackgroundResource(R.drawable.bg_selected_day)
                        }
                        // 일정이 있는 날짜 처리
                        else if (eventsDatesList.contains(data.date)) {
                            // 일정이 있는 날짜는 배경 색상 변경
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

    fun initEvent() {}
}
