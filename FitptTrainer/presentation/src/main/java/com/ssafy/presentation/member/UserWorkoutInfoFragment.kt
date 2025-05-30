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
import com.ssafy.domain.model.measure.CompositionItem
import com.ssafy.domain.model.member.MemberInfo
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentUserWorkoutInfoBinding
import com.ssafy.presentation.member.adapter.UserWorkoutInfoMemberListAdapter
import com.ssafy.presentation.member.adapter.UserWorkoutInfoMonthAdapter
import com.ssafy.presentation.member.adapter.UserWorkoutInfoReportListAdapter
import com.ssafy.presentation.member.viewmodel.UserWorkoutInfoViewModel
import com.ssafy.presentation.util.CommonUtils
import com.ssafy.presentation.util.TimeUtils
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

    private var memberInfos: MemberInfo? = null
    private var memberId : Long? = null
    private val viewModel: UserWorkoutInfoViewModel by viewModels()
    private var selectedMonth = 0

    private var composition = mutableListOf<CompositionItem>()
    private var dateArray = arrayOf<String>()
    private var dateEntries = arrayOf<Double>()

    private val months = listOf("전체", "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월")

    private val userWorkoutInfoMemberListAdapter = UserWorkoutInfoMemberListAdapter { memberInfo ->
        memberInfos = memberInfo
        memberId = memberInfo.memberId

        binding.rvUserReportList.visibility = View.VISIBLE
        binding.tvEmptyReportList.visibility = View.GONE

        memberInfos = memberInfo
        memberId = memberInfo.memberId

        binding.btnSkeletalMuscle.isSelected = true
        binding.btnWeight.isSelected = false
        binding.btnBodyFat.isSelected = false

        dateArray = arrayOf()
        dateEntries = arrayOf()

        viewModel.setSelectedMonth("전체")
        viewModel.getMember(memberInfo.memberId)
        viewModel.getComposition(memberInfo.memberId)
        viewModel.getReports(memberInfo.memberId.toInt())

        val currentMonthText = months[selectedMonth]
        viewModel.setSelectedMonth(currentMonthText)
        monthAdapter.setSelectedPosition(selectedMonth)

        initChart()
    }

    private val useWorkoutInfoReportListAdapter = UserWorkoutInfoReportListAdapter { reportList ->
        val memberId = reportList.memberId.toLong()
        val reportId = reportList.reportId.toLong()

        val action = UserWorkoutInfoFragmentDirections.actionUserWorkoutInfoFragmentToReportEditFragment(
            memberId = memberId,
            reportId = reportId,
            memberName = memberInfos!!.memberName
        )
        findNavController().navigate(action)
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
            viewModel.filteredReports.collect {
                useWorkoutInfoReportListAdapter.submitList(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.reports.collect {
                useWorkoutInfoReportListAdapter.submitList(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.member.collect {
                initMemberView(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.composition.collect {
                Log.d(TAG, "initObserver: ${composition.joinToString(", ")}")
                
                composition = it.groupBy { c -> c.createdAt.substring(0, 10) }
                    .map { (_, items) -> items.maxByOrNull { c -> c.createdAt } }
                    .filterNotNull().toMutableList() // null 값 제거

                updateChartData()
            }
        }
    }

    private fun fetchMembers() {
        viewModel.getMembers()
    }

    private fun initMemberView(memberInfo: MemberInfo?) {
        memberInfo?.let {
            binding.tvUserInfoDetailBirthContent.text = memberInfo.memberBirth
            binding.tvUserInfoDetailGenderContent.text = memberInfo.memberGender
            binding.tvUserInfoDetailHeightContent.text = "${memberInfo.memberHeight}cm"
            binding.tvUserInfoDetailWeightContent.text = "${memberInfo.memberWeight}kg"
        }
    }

    fun initEvent() {
        binding.apply {
            layoutUserReportYear.setOnClickListener {
                if (memberId == null) {
                    showToast("회원을 선택해주세요.")
                    return@setOnClickListener
                }

                showYearDropdownMenu()
            }

            ibUserReportDropdownYear.setOnClickListener {
                if (memberId == null) {
                    showToast("회원을 선택해주세요.")
                    return@setOnClickListener
                }

                showYearDropdownMenu()
            }

            cvAddReport.setOnClickListener {
                if (memberId == null) {
                    showToast("회원을 선택해주세요.")
                    return@setOnClickListener
                }

                memberId?.let {
                    val action = UserWorkoutInfoFragmentDirections.actionUserWorkoutInfoFragmentToReportEditFragment(it)
                    findNavController().navigate(action)
                }
            }

            ibBack.setOnClickListener {
                findNavController().navigate(R.id.action_userWorkoutInfoFragment_to_homeFragment)
            }

            btnSkeletalMuscle.setOnClickListener {
                if (memberId == null) {
                    showToast("회원을 선택해주세요.")
                    return@setOnClickListener
                }

                btnSkeletalMuscle.isSelected = true
                btnWeight.isSelected = false
                btnBodyFat.isSelected = false

                dateArray = composition.map { TimeUtils.formatDateToMonthDay(it.createdAt) }.toTypedArray()
                dateEntries = composition.map { it.smm }.toTypedArray()

                initChart()
            }

            btnWeight.setOnClickListener {
                if (memberId == null) {
                    showToast("회원을 선택해주세요.")
                    return@setOnClickListener
                }

                btnWeight.isSelected = true
                btnSkeletalMuscle.isSelected = false
                btnBodyFat.isSelected = false

                dateArray = composition.map { TimeUtils.formatDateToMonthDay(it.createdAt) }.toTypedArray()
                dateEntries = composition.map { it.weight }.toTypedArray()

                initChart()
            }

            btnBodyFat.setOnClickListener {
                if (memberId == null) {
                    showToast("회원을 선택해주세요.")
                    return@setOnClickListener
                }

                btnBodyFat.isSelected = true
                btnSkeletalMuscle.isSelected = false
                btnWeight.isSelected = false

                dateArray = composition.map { TimeUtils.formatDateToMonthDay(it.createdAt) }.toTypedArray()
                dateEntries = composition.map { it.bfm }.toTypedArray()

                initChart()
            }
        }
    }

    private fun updateChartData() {
        if (composition.isEmpty()) return

        dateArray = composition.map { TimeUtils.formatDateToMonthDay(it.createdAt) }.toTypedArray()

        when {
            binding.btnSkeletalMuscle.isSelected -> {
                dateEntries = composition.map { it.smm }.toTypedArray()
            }
            binding.btnWeight.isSelected -> {
                dateEntries = composition.map { it.weight }.toTypedArray()
            }
            binding.btnBodyFat.isSelected -> {
                dateEntries = composition.map { it.bfm }.toTypedArray()
            }
        }

        initChart()
    }

    fun initRecyclerView() {
        monthAdapter = UserWorkoutInfoMonthAdapter(requireContext(), months) { selectedMonth, position ->
            if (memberId == null) {
                showToast("회원을 선택하세요.")
                false
            } else {
                viewModel.setSelectedMonth(selectedMonth)
                viewModel.filterReportsByYearAndMonth()
                true
            }
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
        Log.d(TAG, "initChart: ${userWorkoutInfoMemberListAdapter.getSelectedItem()}")

        if (userWorkoutInfoMemberListAdapter.getSelectedItem() == null) {
            composition.clear()
            dateArray = arrayOf()
            dateEntries = arrayOf()
        }

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
                val dates = dateArray
                valueFormatter = IndexAxisValueFormatter(dateArray)
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

                setLabelCount(5, true)
            }
        }

        val entries = ArrayList<Entry>()

        dateEntries.forEachIndexed { index, value ->
            entries.add(Entry(index.toFloat(), value.toFloat()))
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


        val currentYear = TimeUtils.getCurrentYear()
        binding.tvUserReportYear.text = "${currentYear}년"
        viewModel.setSelectedYear(currentYear)
        viewModel.setSelectedMonth("전체")
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

            val year = selectedYear.replace("년", "").toInt()
            viewModel.setSelectedYear(year)
            viewModel.filterReportsByYearAndMonth()

            true
        }

        popupMenu.show()
    }
}