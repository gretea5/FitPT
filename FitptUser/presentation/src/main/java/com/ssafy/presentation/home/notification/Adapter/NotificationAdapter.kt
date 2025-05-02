package com.ssafy.presentation.home.notification.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.notification.Notification
import com.ssafy.presentation.databinding.ItemNotificationBinding

class NotificationAdapter(
    private val notificationList: List<Notification>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification) {
            binding.tvNotificationTitle.text = notification.title
            binding.tvNotificationContent.text = notification.content
            binding.tvNotificationDate.text = notification.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notificationList[position])
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }
}