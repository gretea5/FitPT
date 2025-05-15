package com.ssafy.presentation.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.ssafy.domain.model.schedule.Schedule
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentHomeBinding
import com.ssafy.presentation.home.adapter.HomeAdapter
import com.ssafy.presentation.home.viewmodel.HomeStatus
import com.ssafy.presentation.home.viewmodel.HomeViewModel
import com.ssafy.presentation.util.TimeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private const val TAG = "HomeFragment_FitPT"

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind,
    R.layout.fragment_home
) {
    private val eventsDatesList = mutableListOf<LocalDate>()
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var scheduleAdapter : HomeAdapter
    private var selectedDate: LocalDate? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRV()
        initObserver()
        initCalendar()
        initEvent()
        fetchSchedules()
    }

    private fun initRV() {
        binding.rvMemberSchedule.apply {
            scheduleAdapter = HomeAdapter { trainerId ->
                val action = HomeFragmentDirections.actionHomeFragmentToScheduleEditFragment(trainerId)
                findNavController().navigate(action)
            }
            adapter = scheduleAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.homeState.collect { state ->
                when (state) {
                    is HomeStatus.Idle -> {}
                    is HomeStatus.Success -> {
                        Log.d(TAG, "initObserver: ${state.schedules}")
                    }
                    is HomeStatus.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.scheduleItems.collect { scheduleItems ->
                scheduleAdapter.submitList(scheduleItems)

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.monthlyScheduleItems.collect { scheduleItems ->
                    updateEventsDatesList(scheduleItems)
                }
            }
        }
    }

    private fun selectSchedulesByDate(date: LocalDate) {
        selectedDate = date
        val formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        viewModel.getSchedules(
            date = formattedDate,
            month = null,
            trainerId = null,
            memberId = null
        )
    }

    private fun initCalendar() {
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

        val currentMonth = YearMonth.now()

        val startMonth = currentMonth.minusMonths(120)
        val endMonth = currentMonth.plusMonths(120)
        val firstDayOfWeek = firstDayOfWeekFromLocale()

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
                val year = calendarMonth.yearMonth.year
                val month = calendarMonth.yearMonth.monthValue.toString().padStart(2, '0')
                val formattedDate = "$year-$month"

                binding.tvMonthYear.text = formattedDate
                viewModel.getMonthlySchedules(formattedDate)
            }
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

    private fun updateEventsDatesList(scheduleItems: List<Schedule>) {
        eventsDatesList.clear()

        val eventDates = scheduleItems.mapNotNull { scheduleItem ->
            try {
                val dateStr = TimeUtils.parseDateTime(scheduleItem.startTime).first
                LocalDate.parse(dateStr)
            } catch (e: Exception) {
                Log.e(TAG, "날짜 파싱 오류: ${scheduleItem.startTime}, ${e.message}")
                null
            }
        }.distinct()

        eventsDatesList.addAll(eventDates)

        binding.calendar.notifyCalendarChanged()
    }

    fun initEvent() {
        binding.imageButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSchedulingFragment()
            findNavController().navigate(action)
        }
    }
}