package com.ssafy.presentation.schedule

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.FlexboxLayout
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
import com.ssafy.presentation.databinding.FragmentScheduleEditBinding
import com.ssafy.presentation.schedule.viewmodel.ScheduleViewModel
import com.ssafy.presentation.util.TimeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private const val TAG = "ScheduleEditFragment_ssafy"

@AndroidEntryPoint
class ScheduleEditFragment : BaseFragment<FragmentScheduleEditBinding>(
    FragmentScheduleEditBinding::bind,
    R.layout.fragment_schedule_edit
) {
    private val args : ScheduleEditFragmentArgs by navArgs()
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
        "21:00",
        "22:00",
    )

    private val timeButtonsMap = mutableMapOf<String, Button>()
    private var selectedButton: Button? = null

    private var selectedDate: LocalDate? = null
    private var scheduleId: Long? = null
    private var trainerId: Long? = null
    private var memberName: String? = null
    private var startTime: String? = null
    private var endTime: String? = null
    private var scheduleContent: String? = null

    private val clickListener = View.OnClickListener { view ->
        val button = view as Button

        selectedButton?.let { it.isSelected = false }

        if (button.isSelected) {
            button.isSelected = false
            selectedButton = null
        } else {
            button.isSelected = true
            selectedButton = button
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initArgs()
        initCalendar()
        initObserver()
        initButtonView()
        initEvent()
        fetchDaySchedule()
    }

    private fun initArgs() {
        selectedDate = LocalDate.parse(args.selectedDate)

        scheduleId = args.scheduleId
        trainerId = args.trainerId
        memberName = args.memberName
        startTime = args.startTime
        endTime = args.endTime
        scheduleContent = args.scheduleContent

        binding.tvMemberName.text = memberName
        binding.tvTimeInfo.text = "$startTime ~ $endTime"
        binding.etDetail.setText(scheduleContent.toString())
    }

    private fun initCalendar() {
        val currentMonth = YearMonth.now()

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
                        if (day.position == DayPosition.MonthDate && day.date == selectedDate) {
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
                        } else {
                            container.textView.background = null
                        }
                    } else {
                        container.textView.setTextColor(Color.GRAY)
                        container.textView.background = null
                    }
                }
            }

            setup(currentMonth, currentMonth, firstDayOfWeek)
            scrollToMonth(currentMonth)
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
            viewModel.deletedScheduleId.collect { scheduleId ->
                scheduleId?.let {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun initButtonView() {
        morningTimeList.forEach { time ->
            val button = Button(requireContext()).apply {
                layoutParams = FlexboxLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    flexBasisPercent = 0.3f
                    setMargins(4, 4, 4, 4)
                }
                text = time
                setBackgroundResource(R.drawable.selector_button_time)
                setTextColor(Color.BLACK)
                setOnClickListener(clickListener)
            }

            timeButtonsMap[time] = button
            binding.fbMidButton.addView(button)
        }

        afternoonTimeList.forEach { time ->
            val button = Button(requireContext()).apply {
                layoutParams = FlexboxLayout.LayoutParams(0,  ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    flexBasisPercent = 0.3f
                    setMargins(4, 4, 4, 4)
                }
                text = time
                setBackgroundResource(R.drawable.selector_button_time)
                setTextColor(Color.BLACK)
                setOnClickListener(clickListener)
            }

            timeButtonsMap[time] = button
            binding.fbAfternoonButton.addView(button)
        }
    }

    private fun fetchDaySchedule() {
        val date = args.selectedDate

        val formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        viewModel.getSchedules(
            month = null,
            date = formattedDate,
            trainerId = null,
            memberId = null
        )
    }

    private fun updateScheduleButtonColors(
        schedules: List<Schedule>,
        timeButtons: Map<String, Button>
    ) {
        timeButtons.values.forEach { button ->
            button.apply {
                setBackgroundResource(R.drawable.selector_button_time)
                setTextColor(Color.BLACK)
                setOnClickListener(clickListener)
                isEnabled = true
            }
        }

        schedules.forEach { schedule ->
            val timeKey = TimeUtils.parseDateTime(schedule.startTime).second

            timeButtons[timeKey]?.let { button ->
                button.apply {
                    if (timeKey == startTime) {
                        isSelected = true
                        selectedButton = button
                    } else {
                        setBackgroundResource(R.drawable.bg_stroke_gray_8dp)
                        setTextColor(ContextCompat.getColor(button.context, R.color.main_gray))
                        isEnabled = false
                    }
                }
            }
        }
    }

    private fun initEvent() {
        binding.btnDelete.setOnClickListener {
            scheduleId?.let { viewModel.deleteSchedule(it) }
        }
    }
}