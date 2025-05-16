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
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
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
import com.ssafy.data.datasource.UserDataStoreSource
import com.ssafy.domain.model.home.ScheduleInfo
import com.ssafy.domain.model.measure_record.MeasureRecordItem
import com.ssafy.domain.model.measure_record.MesureDetail
import com.ssafy.locket.utils.CalendarUtils.displayText
import com.ssafy.presentation.databinding.CalendarDayBinding
import com.ssafy.presentation.home.viewModel.OpenDialogState
import com.ssafy.presentation.home.viewModel.ScheduleInfoState
import com.ssafy.presentation.home.viewModel.ScheduleViewModel
import com.ssafy.presentation.home.viewModel.SelectedDayState
import com.ssafy.presentation.home.viewModel.SelectedDayViewModel
import com.ssafy.presentation.home.viewModel.UserInfoState
import com.ssafy.presentation.home.viewModel.UserInfoViewModel
import com.ssafy.presentation.measurement_record.adapter.MeasureListAdapter
import com.ssafy.presentation.measurement_record.viewModel.GetBodyListInfoState
import com.ssafy.presentation.measurement_record.viewModel.MeasureViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val TAG = "HomeFragment"
@AndroidEntryPoint
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
    private lateinit var dialog: PtCalendarBottomSheetFragment
    private lateinit var lineChart: LineChart
    private var selectedButton: Button? = null
    private val selectedDayViewModel: SelectedDayViewModel by activityViewModels()

    private val userInfoViewModel: UserInfoViewModel by activityViewModels()
    private val measureViewModel: MeasureViewModel by activityViewModels()
    private val scheduleViewModel : ScheduleViewModel by activityViewModels()

    //일정
    private val scheduleMap = mutableMapOf<LocalDate, List<ScheduleInfo>>()
    //차트
    private val chartDates = mutableListOf<String>() // X축 날짜
    private val weightEntries = mutableListOf<Entry>()
    private val skeletalMuscleEntries = mutableListOf<Entry>()
    private val bodyFatEntries = mutableListOf<Entry>()
    private var click = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeModel()
        initEvent()
        initCalendar()
        lineChart = binding.chartBodyGraph
        setupTabButtons()
        // 초기 선택 버튼 설정 (몸무게)
        selectButton(binding.btnWeight)
        // 차트를 보이게 설정
        lineChart.visibility = View.VISIBLE
        lineChart.setScaleEnabled(false)
        lineChart.setPinchZoom(false)
        lineChart.setDoubleTapToZoomEnabled(false)
    }

    fun initEvent(){
        userInfoViewModel.fetchUser()
        binding.ivNotificationMove.setOnClickListener {
            findNavController().navigate(R.id.action_home_fragment_to_notification_fragment)
        }
        binding.btnPrevMonth.setOnClickListener {
            val currentYearMonth = selectedDayViewModel.selectedYearMonth.value
            val newYearMonth = currentYearMonth.minusMonths(1)
            selectedDayViewModel.setYearMonth(
                newYearMonth
            )
        }

        binding.btnNextMonth.setOnClickListener {
            val currentYearMonth = selectedDayViewModel.selectedYearMonth.value
            val newYearMonth = currentYearMonth.plusMonths(1)
            selectedDayViewModel.setYearMonth(
                newYearMonth
            )
        }
        measureViewModel.getBodyList("createdAt","asc")
        selectedDayViewModel.initYearMonth()
        scheduleViewModel.getScheduleList("",selectedDayViewModel.selectedYearMonth.value.toString())
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

    private fun bindDate(
        date: LocalDate,
        dayText: TextView,
        scheduleText: TextView,
        isSelectable: Boolean
    ) {
        dayText.text = date.dayOfMonth.toString()
        val fonts = arrayOf(R.font.pretendard_regular, R.font.pretendard_bold)
        val schedules = scheduleMap[date]
        if (!schedules.isNullOrEmpty()) {
            scheduleText.text = "• ${schedules.size}개"
        } else {
            scheduleText.text = ""
        }
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
            scheduleText.setText(null)
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
        updateDayWeekColor()
        initObserver()
    }

    private fun dateClicked(date: LocalDate) {
        if(selectedDayViewModel.selectedDay.value is SelectedDayState.Exist == false) {
            val schedules = scheduleMap[date]
            binding.calendar.notifyDateChanged(selectedDate) // 이전 선택값 해제
            selectedDate = date
            binding.calendar.notifyDateChanged(date) // 새로운 선택값
            if (!schedules.isNullOrEmpty()) {
                selectedDayViewModel.setSelectedDay(date)
            } else {
                selectedDayViewModel.clearSelectedDay() // 상태 초기화 (선택만 되고 BottomSheet X)
                Log.d(TAG, "선택한 날짜($date)에 스케줄 없음 → BottomSheet 열지 않음")
            }
        }
    }

    private fun initObserver() {
        // 선택된 날짜 갱신
        viewLifecycleOwner.lifecycleScope.launch {
            selectedDayViewModel.selectedDay.collectLatest { uiState ->
                if (uiState is SelectedDayState.Exist) {
                    binding.calendar.notifyDateChanged(selectedDate)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            selectedDayViewModel.openDialog.collectLatest { dialogState ->
                if (dialogState is OpenDialogState.Opened && !dialog.isAdded) {
                    val schedules = scheduleMap[selectedDate]
                    if (!schedules.isNullOrEmpty()) {
                        val dialog = PtCalendarBottomSheetFragment.newInstance(ArrayList(schedules))
                        dialog.show(childFragmentManager, "payment")
                    } else {
                        Log.d(TAG, "openDialog 트리거되었지만 해당 날짜에 스케줄 없음")
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userInfoViewModel.userInfo.collect { user ->
                    if (user is UserInfoState.Success) {
                        Log.d(TAG,user.userInfo.toString())
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedDayViewModel.selectedYearMonth.collectLatest { yearMonth ->
                    binding.calendar.scrollToMonth(yearMonth)
                    scheduleViewModel.getScheduleList("",selectedDayViewModel.selectedYearMonth.value.toString())
                    if (yearMonth >= YearMonth.of(today.year, today.monthValue)) {
                        binding.btnNextMonth.isEnabled = false
                    } else if (yearMonth < YearMonth.of(2020, 2)) {
                        binding.btnPrevMonth.isEnabled = false
                    } else {
                        binding.btnPrevMonth.isEnabled = true
                        binding.btnNextMonth.isEnabled = true
                    }
                    // 상단 텍스트 갱신
                    binding.tvYearMonth.text = getString(R.string.calendar_year_month, yearMonth.year, yearMonth.monthValue)
                }
            }
        }
    }

    private fun updateScheduleMap(scheduleList: List<ScheduleInfo>) {
        scheduleMap.clear()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        scheduleMap.putAll(
            scheduleList.groupBy {
                LocalDateTime.parse(it.startTime, formatter).toLocalDate()
            }
        )
    }


    private fun setupTabButtons() {
        val buttons = listOf(
            binding.btnSkeletalMuscle,
            binding.btnWeight,
            binding.btnBodyFat
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                selectButton(button)
                // 여기에 각 버튼에 맞는 데이터/화면 변경 로직 추가
                when (button.id) {
                    R.id.btn_skeletal_muscle -> {
                      showSkeletalMuscleData()
                    }
                    R.id.btn_weight -> {
                        showWeightData()
                    }
                    R.id.btn_body_fat -> {
                        showBodyFatData()
                    }
                }
            }
        }
    }
    private fun selectButton(button: Button) {
        selectedButton?.isSelected = false
        button.isSelected = true
        selectedButton = button
    }

    private fun showChart(
        entries: List<Entry>,
        label: String,
        colorHex: String,
        fillDrawableRes: Int,
        yUnit: String,
        isPercent: Boolean = false
    ) {
        Log.d(TAG, "$label Entries: $entries")

        val dataSet = LineDataSet(entries, label).apply {
            color = Color.parseColor(colorHex)
            lineWidth = 2.5f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillDrawable = ContextCompat.getDrawable(requireContext(), fillDrawableRes)
            setDrawCircles(true)
            setCircleColor(Color.WHITE)
            circleRadius = 4f
            setCircleHoleColor(Color.parseColor(colorHex))
            circleHoleRadius = 2f
            setDrawValues(false)
        }

        lineChart.apply {
            data = LineData(dataSet)
            setupYAxis(entries, yUnit, isPercent)
            setupXAxis()
            axisRight.isEnabled = false
            setDrawGridBackground(false)
            invalidate()
        }
    }

    private fun setupYAxis(entries: List<Entry>, unit: String, isPercent: Boolean = false) {
        if (entries.isEmpty()) {
            Log.w(TAG, "setupYAxis: entries가 비어 있습니다.")
            lineChart.axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 100f // 기본값 설정 (단위나 차트 종류에 따라 조절 가능)
                granularity = 25f
                setLabelCount(5, true)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return if (isPercent) "${value.toInt()}%" else "${value.toInt()} $unit"
                    }
                }
            }
            return
        }
        val minY = entries.minOf { it.y }
        val maxY = entries.maxOf { it.y }
        val buffer = (maxY - minY) * 0.2f
        lineChart.axisLeft.apply {
            axisMinimum = (minY - buffer).coerceAtLeast(0f)
            axisMaximum = maxY + buffer
            granularity = ((maxY - minY) / 4).coerceAtLeast(1f)
            setLabelCount(5, true)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (isPercent) "${value.toInt()}%" else "${value.toInt()} $unit"
                }
            }
        }
    }

    private fun setupXAxis() {
        lineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            textColor = Color.parseColor("#AAAAAA")
            textSize = 12f
            granularity = 1f
            setDrawGridLines(false)
            valueFormatter = IndexAxisValueFormatter(chartDates)
        }
    }

    private fun showWeightData() {
        showChart(
            entries = weightEntries,
            label = "체중(kg)",
            colorHex = "#FF5722",
            fillDrawableRes = R.drawable.fade_red,
            yUnit = "kg"
        )
    }

    private fun showSkeletalMuscleData() {
        showChart(
            entries = skeletalMuscleEntries,
            label = "골격근량(kg)",
            colorHex = "#4CAF50",
            fillDrawableRes = R.drawable.fade_red,
            yUnit = "kg"
        )
    }

    private fun showBodyFatData() {
        showChart(
            entries = bodyFatEntries,
            label = "체지방량(%)",
            colorHex = "#2196F3",
            fillDrawableRes = R.drawable.fade_red,
            yUnit = "%",
            isPercent = true
        )
    }

    fun observeModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userInfoViewModel.userInfo.collect { user ->
                    if (user is UserInfoState.Success) {
                        binding.tvBodyGraph.text = user.userInfo.memberName+"님의 체성분 그래프"
                    }
                    else{
                        Log.d(TAG,"실패")
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                measureViewModel.getBodyListInfo.collect { state ->
                    when (state) {
                        is GetBodyListInfoState.Loading -> {
                            Log.d(TAG, "로딩 중...")
                        }
                        is GetBodyListInfoState.Success -> {
                            val list = state.getBodyList
                            Log.d(TAG, "데이터 수신 성공: $list")

                            // 데이터 정렬 (날짜 순)
                            val sortedList = list.sortedBy { it.weight }

                            chartDates.clear()
                            weightEntries.clear()
                            skeletalMuscleEntries.clear()
                            bodyFatEntries.clear()

                            sortedList.forEachIndexed { index, bodyInfo ->
                                try {
                                    val outputFormatter = DateTimeFormatter.ofPattern("MM-dd")
                                    val dateTime = LocalDateTime.parse(bodyInfo.createdAt) // ISO 형식 자동 파싱
                                    val formattedDate = dateTime.format(outputFormatter)
                                    chartDates.add(formattedDate)
                                    weightEntries.add(Entry(index.toFloat(), bodyInfo.weight.toFloat()))
                                    skeletalMuscleEntries.add(Entry(index.toFloat(), bodyInfo.bfp.toFloat()))
                                    bodyFatEntries.add(Entry(index.toFloat(), bodyInfo.smm.toFloat()))
                                } catch (e: Exception) {
                                    Log.e(TAG, "날짜 파싱 오류: ${e.message}")
                                }
                            }
                            if(!click){
                                showWeightData()
                                click = true
                            }
                        }
                        is GetBodyListInfoState.Error -> {
                            Log.d(TAG, "에러: ${state.message}")
                        }

                        else -> Unit
                    }
                }
            }
        }
        // 일정 관련 코드
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                scheduleViewModel.scheduleInfo.collect { schedule ->
                    if (schedule is ScheduleInfoState.Success) {
                        Log.d(TAG, schedule.scheduleList.toString())
                        updateScheduleMap(schedule.scheduleList)
                        binding.calendar.notifyCalendarChanged() // 화면 다시 갱신
                    }
                    else{
                        Log.d(TAG,"실패")
                    }
                }
            }
        }
    }
}

