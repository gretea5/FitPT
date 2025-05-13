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
import com.ssafy.locket.utils.CalendarUtils.displayText
import com.ssafy.presentation.databinding.CalendarDayBinding
import com.ssafy.presentation.home.viewModel.OpenDialogState
import com.ssafy.presentation.home.viewModel.SelectedDayState
import com.ssafy.presentation.home.viewModel.SelectedDayViewModel
import com.ssafy.presentation.home.viewModel.UserInfoState
import com.ssafy.presentation.home.viewModel.UserInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.time.DayOfWeek
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
    val currentMonth = YearMonth.of(2025,5)
    private lateinit var dialog: PtCalendarBottomSheetFragment
    private lateinit var lineChart: LineChart
    private var selectedButton: Button? = null
    private val selectedDayViewModel: SelectedDayViewModel by activityViewModels()

    private val userInfoViewModel: UserInfoViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeModel()
        initEvent()
        initCalendar()
        //initView()
        lineChart = binding.chartBodyGraph
        setupLineChart()
        setLineChartData()
        setupTabButtons()
        // 초기 선택 버튼 설정 (몸무게)
        selectButton(binding.btnWeight)
        // 차트를 보이게 설정
        lineChart.visibility = View.VISIBLE
    }

    fun initEvent(){
        Log.d(TAG,"실행")
        userInfoViewModel.fetchUser()
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
        if(selectedDayViewModel.selectedDay.value is SelectedDayState.Exist == false) {
            binding.calendar.notifyDateChanged(selectedDate) // 이전 선택값 해제
            selectedDate = date
            binding.calendar.notifyDateChanged(date) // 새로운 선택값
            selectedDayViewModel.setSelectedDay(date)
            //selectedDayViewModel.setSelectedDayPayments(date.year, date.monthValue, date.dayOfMonth)
        }
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
        initObserver()
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
                    dialog.show(childFragmentManager, "payment")
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
    }

    /*
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
    }*/

    private fun setupTabButtons() {
        val buttons = listOf(
            binding.btnSkeletalMuscle,
            binding.btnWeight,
            binding.btnBmi,
            binding.btnBodyFat
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                selectButton(button)

                // 여기에 각 버튼에 맞는 데이터/화면 변경 로직 추가
                when (button.id) {
                    R.id.btn_skeletal_muscle -> {
                        // 골격근량 데이터 표시
                    }
                    R.id.btn_weight -> {
                        // 몸무게 데이터 표시
                    }
                    R.id.btn_bmi -> {
                        // BMI 데이터 표시
                    }
                    R.id.btn_body_fat -> {
                        // 체지방량 데이터 표시
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


    //차트 관련한 코드
    private fun setupLineChart() {
        lineChart.apply {
            // 차트 설명 텍스트 숨기기
            description.isEnabled = false

            // 차트 오른쪽 Y축 숨기기
            axisRight.isEnabled = false

            // 터치 제스처 설정
            setTouchEnabled(true)
            setPinchZoom(false)
            setScaleEnabled(false)

            // 배경 격자 설정
            setDrawGridBackground(false)

            // 범례 숨기기
            legend.isEnabled = false

            // X축 설정
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.parseColor("#AAAAAA")
                textSize = 12f
                granularity = 1f
                setDrawGridLines(false)

                // X축 날짜 데이터 설정
                val dates = arrayOf("01/10", "01/25", "02/11", "02/19", "03/13", "03/27", "04/01", "04/12", "04/22")
                valueFormatter = IndexAxisValueFormatter(dates)
            }

            // Y축 설정
            axisLeft.apply {
                textColor = Color.parseColor("#AAAAAA")
                textSize = 12f
                granularity = 25f
                axisMinimum = 0f
                axisMaximum = 100f
                setDrawGridLines(true)
                gridColor = Color.parseColor("#DDDDDD")
                gridLineWidth = 0.5f

                // Y축 수치 설정 (0, 25, 50, 75, 100)
                setLabelCount(5, true)
            }

            // 애니메이션 설정
            //animateX(1000)
        }
    }

    private fun setLineChartData() {
        // 데이터 포인트 생성
        val entries = ArrayList<Entry>().apply {
            add(Entry(0f, 10f))    // 01/10, 값: 10
            add(Entry(1f, 30f))    // 01/25, 값: 30
            add(Entry(2f, 50f))    // 02/11, 값: 50
            add(Entry(3f, 45f))    // 02/19, 값: 45
            add(Entry(4f, 40f))    // 03/13, 값: 40
            add(Entry(5f, 60f))    // 03/27, 값: 60
            add(Entry(6f, 80f))    // 04/01, 값: 80
            add(Entry(7f, 75f))    // 04/12, 값: 75
            add(Entry(8f, 85f))    // 04/22, 값: 85
        }

        // 데이터셋 생성 및 스타일 설정
        val dataSet = LineDataSet(entries, "데이터셋").apply {
            // 선 스타일 설정
            color = Color.parseColor("#FF5722")  // 주황-빨강 계열 색상
            lineWidth = 2.5f
            mode = LineDataSet.Mode.CUBIC_BEZIER  // 곡선으로 표현

            // 선 아래 영역 채우기 설정
            setDrawFilled(true)
            fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_red)

            // 원형 포인트 설정
            setDrawCircles(true)
            setCircleColor(Color.WHITE)
            circleRadius = 4f
            setCircleHoleColor(Color.parseColor("#FF5722"))
            circleHoleRadius = 2f

            // 값 텍스트 숨기기
            setDrawValues(false)
        }

        // 차트에 데이터 설정
        lineChart.data = LineData(dataSet)
        lineChart.invalidate()  // 차트 갱신
    }

    fun observeModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userInfoViewModel.userInfo.collect { user ->
                    if (user is UserInfoState.Success) {

                        binding.tvBodyGraph.text = user.userInfo.memberName+"님의 체성분 그래프"
                        binding.tvPtCalendar.text = user.userInfo.memberName+"님의 PT 일정"
                    }
                    else{
                        Log.d(TAG,"실패")
                    }
                }
            }
        }
    }
}