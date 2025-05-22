package com.ssafy.presentation.home.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.domain.model.notification.Notification
import com.ssafy.presentation.R
import com.ssafy.presentation.base.BaseFragment
import com.ssafy.presentation.databinding.FragmentHomeBinding
import com.ssafy.presentation.databinding.FragmentNotificationBinding
import com.ssafy.presentation.home.notification.Adapter.NotificationAdapter

class NotificationFragment : BaseFragment<FragmentNotificationBinding>(
    FragmentNotificationBinding::bind,
    R.layout.fragment_notification
) {
    private lateinit var notificationAdapter: NotificationAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initAdapter()
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    fun initAdapter(){
        val notificationList = listOf(
            Notification("PT 보고서 알림", "트레이너가 작성한 PT 일일 보고서가 도착했어요.\n확인해 보세요!", "5월 3일"),
            Notification("PT 일정 변경", "PT 일정이 변경되었습니다. 확인해주세요.", "5월 4일")
        )
        notificationAdapter = NotificationAdapter(notificationList)
        binding.rvRecentNotification.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notificationAdapter
        }
    }
}