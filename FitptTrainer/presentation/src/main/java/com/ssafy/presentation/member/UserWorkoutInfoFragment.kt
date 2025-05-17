package com.ssafy.presentation.member

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentUserWorkoutInfoBinding
import com.ssafy.presentation.member.adapter.UserWorkoutInfoMemberListAdapter
import com.ssafy.presentation.member.adapter.UserWorkoutInfoMonthAdapter
import com.ssafy.presentation.member.adapter.UserWorkoutInfoReportListAdapter
import com.ssafy.presentation.member.viewmodel.UserWorkoutInfoViewModel
import com.ssafy.presentation.util.CommonUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "UserWorkoutInfoFragment_FitPT"

@AndroidEntryPoint
class UserWorkoutInfoFragment : BaseFragment<FragmentUserWorkoutInfoBinding>(
    FragmentUserWorkoutInfoBinding::bind,
    R.layout.fragment_user_workout_info
) {
    private lateinit var monthAdapter: UserWorkoutInfoMonthAdapter
    private lateinit var lineChart: LineChart

    private var memberId : Long? = null
    private val viewModel: UserWorkoutInfoViewModel by viewModels()
    private val userWorkoutInfoMemberListAdapter = UserWorkoutInfoMemberListAdapter { memberInfo ->
        memberId = memberInfo.memberId

        viewModel.getReports(memberInfo.memberId.toInt())
    }

    private val useWorkoutInfoReportListAdapter = UserWorkoutInfoReportListAdapter { reportList ->
        //보고서 상세 조회 들어가는 로직 추가(예정) => Navigation 추가
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSetting()
        initObserver()
        initRecyclerView()
        initChart()
        initEvent()
        fetchMembers()
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.members.collect {
                userWorkoutInfoMemberListAdapter.submitList(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.reports.collect {
                useWorkoutInfoReportListAdapter.submitList(it)
            }
        }
    }

    private fun fetchMembers() {
        viewModel.getMembers()
    }

    fun initEvent() {
        binding.apply {

            layoutUserReportYear.setOnClickListener {
                showYearDropdownMenu()
            }

            ibUserReportDropdownYear.setOnClickListener {
                showYearDropdownMenu()
            }

            cvAddReport.setOnClickListener {
                memberId?.let {
                    val action = UserWorkoutInfoFragmentDirections.actionUserWorkoutInfoFragmentToReportEditFragment(it)
                    findNavController().navigate(action)
                }
            }

            ibBack.setOnClickListener {
                findNavController().navigate(R.id.action_userWorkoutInfoFragment_to_homeFragment)
            }
        }
    }

    fun initRecyclerView() {
        val months = listOf("전체", "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월")

        monthAdapter = UserWorkoutInfoMonthAdapter(requireContext(), months) { selectedMonth ->
            Log.d(TAG, "initRecyclerView: ${selectedMonth}")
            // 여기서 클릭된 월에 따라 동작 처리
        }

        binding.rvUserReportMonth.apply {
            adapter = monthAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvMemberList.apply {
            adapter = userWorkoutInfoMemberListAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        binding.rvUserReportList.apply {
            adapter = useWorkoutInfoReportListAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    fun initChart() {
        lineChart = binding.chartUserBodyGraph

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

    fun initSetting() {
        binding.apply {
            val changeText = CommonUtils.changeTextColor(
                context = requireContext(),
                fullText = "PT 회원 리스트",
                changeText = "PT",
                color = R.color.highlight_orange
            )

            tvUserUserListTitle.text = changeText
        }
    }

    private fun showYearDropdownMenu() {
        val yearList = listOf("2025년", "2024년", "2023년", "2022년")

        val popupMenu = PopupMenu(requireContext(), binding.tvUserReportYear)
        yearList.forEachIndexed { index, year ->
            popupMenu.menu.add(0, index, index, year)
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val selectedYear = yearList[menuItem.itemId]
            binding.tvUserReportYear.text = selectedYear
            // 선택된 연도에 따라 데이터를 필터링하거나 업데이트하는 로직 추가
            true
        }

        popupMenu.show()
    }
}